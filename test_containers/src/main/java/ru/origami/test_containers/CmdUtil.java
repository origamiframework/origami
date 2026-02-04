package ru.origami.test_containers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.origami.common.environment.Environment.getSysEnvPropertyOrDefault;
import static ru.origami.common.environment.Language.getLangValue;

public class CmdUtil {

    private static final String CI_REPOSITORIES_DIR = "REPOSITORIES_DIR";
    private static final String DEFAULT_REPOSITORIES_DIR = "repositories";
    public static final String REPOSITORIES_DIR = getSysEnvPropertyOrDefault(CI_REPOSITORIES_DIR, CI_REPOSITORIES_DIR, DEFAULT_REPOSITORIES_DIR);

    private static String[] resolveMavenCmd(File workDir) {
        File mvnw = new File(workDir, "mvnw");

        if (mvnw.exists() && mvnw.canExecute()) {
            return new String[] { "./mvnw" };
        }

        File mvnwCmd = new File(workDir, "mvnw.cmd");

        if (mvnwCmd.exists()) {
            return new String[] { "mvnw.cmd" };
        }

        String override = System.getenv("MVN_CMD");

        if (override != null && !override.isBlank()) {
            return override.trim().split("\\s+");
        }

        return new String[] { "mvn" };
    }

    private static void runInDir(File dir, List<String> cmd) {
        try {
            Process p = new ProcessBuilder(cmd)
                    .directory(dir)
                    .inheritIO()
                    .start();
            int code = p.waitFor();

            if (code != 0) {
                throw new RuntimeException(getLangValue("test.containers.cmd.command.failed")
                        .formatted(code, String.join(" ", cmd)));
            }
        } catch (Exception e) {
            throw new RuntimeException(getLangValue("test.containers.cmd.failed.to.run")
                    .formatted(dir, String.join(" ", cmd)), e);
        }
    }

    public static void ensureServiceJarBuilt(String jarName) {
        File dir = new File(REPOSITORIES_DIR, jarName);

        if (!dir.isDirectory()) {
            throw new IllegalStateException(getLangValue("test.containers.cmd.service.sources.not.found")
                    .formatted(dir.getAbsolutePath()));
        }

        File jar = new File(dir, "target/%s.jar".formatted(jarName));

        if (jar.exists()) {
            return;
        }

        String[] mvnCmd = resolveMavenCmd(dir);
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(mvnCmd));
        cmd.addAll(Arrays.asList("-B", "-ntp", "-DskipTests", "clean", "package"));

        runInDir(dir, cmd);

        if (!jar.exists()) {
            File target = new File(dir, "target");
            File[] jars = target.listFiles((d, name) -> name.endsWith(".jar") && !name.endsWith(".original"));

            if (jars == null || jars.length == 0) {
                throw new IllegalStateException(getLangValue("test.containers.cmd.service.jar.not.found").formatted(target.getAbsolutePath()));
            }

            if (!jars[0].renameTo(jar)) {
                throw new IllegalStateException(getLangValue("test.containers.cmd.failed.to.rename")
                        .formatted(jars[0].getName(), jarName));
            }
        }
    }
}
