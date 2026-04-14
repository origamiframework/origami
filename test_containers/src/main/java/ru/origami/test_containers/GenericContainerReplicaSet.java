package ru.origami.test_containers;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.containers.*;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.startupcheck.StartupCheckStrategy;
import org.testcontainers.containers.traits.LinkableContainer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.images.ImagePullPolicy;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static ru.origami.common.environment.Environment.EXECUTION_PARALLEL;

public class GenericContainerReplicaSet {

    private List<Startable> containers = new ArrayList<>();

    private List<GenericContainer<?>> genericContainers = new ArrayList<>();

    public GenericContainerReplicaSet(Startable container, int replicaCount) {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            for (int i = 0; i < replicaCount; i++) {
                containers.add(container);
            }
        } else {
            containers.add(container);
        }
    }

    public GenericContainerReplicaSet(GenericContainer<?> container, int replicaCount) {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            for (int i = 0; i < replicaCount; i++) {
                genericContainers.add(container);
            }
        } else {
            genericContainers.add(container);
        }
    }

    public GenericContainerReplicaSet(DockerImageName dockerImageName, int replicaCount) {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            for (int i = 0; i < replicaCount; i++) {
                genericContainers.add(new GenericContainer<>(dockerImageName));
            }
        } else {
            genericContainers.add(new GenericContainer<>(dockerImageName));
        }
    }

    public GenericContainerReplicaSet(Future<String> image, int replicaCount) {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            for (int i = 0; i < replicaCount; i++) {
                genericContainers.add(new GenericContainer<>(image));
            }
        } else {
            genericContainers.add(new GenericContainer<>(image));
        }
    }

    public GenericContainerReplicaSet(RemoteDockerImage image, int replicaCount) {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            for (int i = 0; i < replicaCount; i++) {
                genericContainers.add(new GenericContainer<>(image));
            }
        } else {
            genericContainers.add(new GenericContainer<>(image));
        }
    }

    public GenericContainerReplicaSet(String dockerImageName, int replicaCount) {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            for (int i = 0; i < replicaCount; i++) {
                genericContainers.add(new GenericContainer<>(dockerImageName));
            }
        } else {
            genericContainers.add(new GenericContainer<>(dockerImageName));
        }
    }

    public List<Startable> getContainers() {
        return new ArrayList<>(containers);
    }

    public List<GenericContainer<?>> getGenericContainers() {
        return new ArrayList<>(genericContainers);
    }

    public GenericContainerReplicaSet setImage(Future<String> image) {
        genericContainers.forEach(c -> c.setImage(image));

        return this;
    }

    public List<Integer> getExposedPorts() {
        return genericContainers.getFirst().getExposedPorts();
    }

    public GenericContainerReplicaSet setExposedPorts(List<Integer> exposedPorts) {
        genericContainers.forEach(c -> c.setExposedPorts(exposedPorts));

        return this;
    }

    public GenericContainerReplicaSet dependsOn(Startable... startables) {
        genericContainers.forEach(c -> c.dependsOn(startables));

        return this;
    }

    public GenericContainerReplicaSet dependsOn(List<? extends Startable> startables) {
        genericContainers.forEach(c -> c.dependsOn(startables));

        return this;
    }

    public GenericContainerReplicaSet dependsOn(Iterable<? extends Startable> startables) {
        genericContainers.forEach(c -> c.dependsOn(startables));

        return this;
    }

    public List<String> getContainerIds() {
        return genericContainers.stream().map(GenericContainer::getContainerId).toList();
    }

    public void start() {
        genericContainers.forEach(GenericContainer::start);
    }

    public void stop() {
        genericContainers.forEach(GenericContainer::stop);
    }

    public List<Set<Integer>> getLivenessCheckPortNumbers() {
        return genericContainers.stream().map(GenericContainer::getLivenessCheckPortNumbers).toList();
    }

    public GenericContainerReplicaSet waitingFor(WaitStrategy waitStrategy) {
        genericContainers.forEach(c -> c.waitingFor(waitStrategy));

        return this;
    }

    public GenericContainerReplicaSet setWaitStrategy(WaitStrategy waitStrategy) {
        genericContainers.forEach(c -> c.setWaitStrategy(waitStrategy));

        return this;
    }

    public GenericContainerReplicaSet setCommand(String command) {
        genericContainers.forEach(c -> c.setCommand(command));

        return this;
    }

    public GenericContainerReplicaSet setCommand(String... commandParts) {
        genericContainers.forEach(c -> c.setCommand(commandParts));

        return this;
    }

    public Map<String, String> getEnvMap() {
        return genericContainers.getFirst().getEnvMap();
    }

    public List<String> getEnv() {
        return genericContainers.getFirst().getEnv();
    }

    public GenericContainerReplicaSet setEnv(List<String> env) {
        genericContainers.forEach(c -> c.setEnv(env));

        return this;
    }

    public GenericContainerReplicaSet addEnv(String key, String value) {
        genericContainers.forEach(c -> c.addEnv(key, value));

        return this;
    }

    public GenericContainerReplicaSet addFileSystemBind(String hostPath, String containerPath, BindMode mode, SelinuxContext selinuxContext) {
        genericContainers.forEach(c -> c.addFileSystemBind(hostPath, containerPath, mode, selinuxContext));

        return this;
    }

    public GenericContainerReplicaSet withFileSystemBind(String hostPath, String containerPath, BindMode mode) {
        genericContainers.forEach(c -> c.withFileSystemBind(hostPath, containerPath, mode));

        return this;
    }

    public GenericContainerReplicaSet withVolumesFrom(Container container, BindMode mode) {
        genericContainers.forEach(c -> c.withVolumesFrom(container, mode));

        return this;
    }

    @Deprecated
    public GenericContainerReplicaSet addLink(LinkableContainer otherContainer, String alias) {
        genericContainers.forEach(c -> c.addLink(otherContainer, alias));

        return this;
    }

    public GenericContainerReplicaSet addExposedPort(Integer port) {
        genericContainers.forEach(c -> c.addExposedPort(port));

        return this;
    }

    public GenericContainerReplicaSet addExposedPorts(int... ports) {
        genericContainers.forEach(c -> c.addExposedPorts(ports));

        return this;
    }

    @Deprecated
    public List<Statement> apply(Statement base, Description description) {
        return genericContainers.stream().map(c -> c.apply(base, description)).toList();
    }

    public GenericContainerReplicaSet withExposedPorts(Integer... ports) {
        genericContainers.forEach(c -> c.withExposedPorts(ports));

        return this;
    }

    public GenericContainerReplicaSet withEnv(String key, String value) {
        genericContainers.forEach(c -> c.withEnv(key, value));

        return this;
    }

    public GenericContainerReplicaSet withEnv(Map<String, String> env) {
        genericContainers.forEach(c -> c.withEnv(env));

        return this;
    }

    public GenericContainerReplicaSet withLabel(String key, String value) {
        genericContainers.forEach(c -> c.withLabel(key, value));

        return this;
    }

    public GenericContainerReplicaSet withLabels(Map<String, String> labels) {
        genericContainers.forEach(c -> c.withLabels(labels));

        return this;
    }

    public GenericContainerReplicaSet withCommand(String cmd) {
        genericContainers.forEach(c -> c.withCommand(cmd));

        return this;
    }

    public GenericContainerReplicaSet withCommand(String... commandParts) {
        genericContainers.forEach(c -> c.withCommand(commandParts));

        return this;
    }

    public GenericContainerReplicaSet withExtraHost(String hostname, String ipAddress) {
        genericContainers.forEach(c -> c.withExtraHost(hostname, ipAddress));

        return this;
    }

    public GenericContainerReplicaSet withNetworkMode(String networkMode) {
        genericContainers.forEach(c -> c.withNetworkMode(networkMode));

        return this;
    }

    public GenericContainerReplicaSet withNetwork(Network network) {
        genericContainers.forEach(c -> c.withNetwork(network));

        return this;
    }

    public GenericContainerReplicaSet withNetworkAliases(String... aliases) {
        genericContainers.forEach(c -> c.withNetworkAliases(aliases));

        return this;
    }

    public GenericContainerReplicaSet withImagePullPolicy(ImagePullPolicy imagePullPolicy) {
        genericContainers.forEach(c -> c.withImagePullPolicy(imagePullPolicy));

        return this;
    }

    public GenericContainerReplicaSet withClasspathResourceMapping(String resourcePath, String containerPath, BindMode mode) {
        genericContainers.forEach(c -> c.withClasspathResourceMapping(resourcePath, containerPath, mode));

        return this;
    }

    public GenericContainerReplicaSet withClasspathResourceMapping(String resourcePath, String containerPath,
                                                                   BindMode mode, SelinuxContext selinuxContext) {
        genericContainers.forEach(c -> c.withClasspathResourceMapping(resourcePath, containerPath, mode, selinuxContext));

        return this;
    }

    public GenericContainerReplicaSet withStartupTimeout(Duration startupTimeout) {
        genericContainers.forEach(c -> c.withStartupTimeout(startupTimeout));

        return this;
    }

    public GenericContainerReplicaSet withPrivilegedMode(boolean mode) {
        genericContainers.forEach(c -> c.withPrivilegedMode(mode));

        return this;
    }

    public GenericContainerReplicaSet withMinimumRunningDuration(Duration minimumRunningDuration) {
        genericContainers.forEach(c -> c.withMinimumRunningDuration(minimumRunningDuration));

        return this;
    }

    public GenericContainerReplicaSet withStartupCheckStrategy(StartupCheckStrategy strategy) {
        genericContainers.forEach(c -> c.withStartupCheckStrategy(strategy));

        return this;
    }

    public GenericContainerReplicaSet withWorkingDirectory(String workDir) {
        genericContainers.forEach(c -> c.withWorkingDirectory(workDir));

        return this;
    }

    public GenericContainerReplicaSet withCopyFileToContainer(MountableFile mountableFile, String containerPath) {
        genericContainers.forEach(c -> c.withCopyFileToContainer(mountableFile, containerPath));

        return this;
    }

    public GenericContainerReplicaSet withCopyToContainer(Transferable transferable, String containerPath) {
        genericContainers.forEach(c -> c.withCopyToContainer(transferable, containerPath));

        return this;
    }

    public GenericContainerReplicaSet setDockerImageName(String dockerImageName) {
        genericContainers.forEach(c -> c.setDockerImageName(dockerImageName));

        return this;
    }

    public String getDockerImageName() {
        return genericContainers.getFirst().getDockerImageName();
    }

    public GenericContainerReplicaSet withLogConsumer(Consumer<OutputFrame> consumer) {
        genericContainers.forEach(c -> c.withLogConsumer(consumer));

        return this;
    }

    public void copyFileFromContainer(String containerPath, String destinationPath) {
        genericContainers.forEach(c -> c.copyFileFromContainer(containerPath, destinationPath));
    }

    public GenericContainerReplicaSet withStartupAttempts(int attempts) {
        genericContainers.forEach(c -> c.withStartupAttempts(attempts));

        return this;
    }

    public GenericContainerReplicaSet withCreateContainerCmdModifier(Consumer<CreateContainerCmd> modifier) {
        genericContainers.forEach(c -> c.withCreateContainerCmdModifier(modifier));

        return this;
    }

    public GenericContainerReplicaSet withSharedMemorySize(Long bytes) {
        genericContainers.forEach(c -> c.withSharedMemorySize(bytes));

        return this;
    }

    public GenericContainerReplicaSet withTmpFs(Map<String, String> mapping) {
        genericContainers.forEach(c -> c.withTmpFs(mapping));

        return this;
    }

    public GenericContainerReplicaSet withReuse(boolean reusable) {
        genericContainers.forEach(c -> c.withReuse(reusable));

        return this;
    }

    public GenericContainerReplicaSet withAccessToHost(boolean value) {
        genericContainers.forEach(c -> c.withAccessToHost(value));

        return this;
    }

    public List<String> getContainerNames() {
        return genericContainers.stream().map(GenericContainer::getContainerName).toList();
    }

    public Network getNetwork() {
        return genericContainers.getFirst().getNetwork();
    }

    public List<Bind> getBinds() {
        return genericContainers.getFirst().getBinds();
    }

    public GenericContainerReplicaSet setBinds(List<Bind> binds) {
        genericContainers.forEach(c -> c.setBinds(binds));

        return this;
    }

    public String[] getCommandParts() {
        return genericContainers.getFirst().getCommandParts();
    }

    public GenericContainerReplicaSet setCommandParts(String[] commandParts) {
        genericContainers.forEach(c -> c.setCommandParts(commandParts));

        return this;
    }

    public List<String> getNetworkAliases() {
        return genericContainers.getFirst().getNetworkAliases();
    }

    public GenericContainerReplicaSet setNetworkAliases(List<String> aliases) {
        genericContainers.forEach(c -> c.setNetworkAliases(aliases));

        return this;
    }

    public List<String> getPortBindings() {
        return genericContainers.getFirst().getPortBindings();
    }

    public GenericContainerReplicaSet setPortBindings(List<String> portBindings) {
        genericContainers.forEach(c -> c.setPortBindings(portBindings));

        return this;
    }

    public GenericContainerReplicaSet setPrivilegedMode(boolean mode) {
        genericContainers.forEach(c -> c.setPrivilegedMode(mode));

        return this;
    }

    public boolean isPrivilegedMode() {
        return genericContainers.getFirst().isPrivilegedMode();
    }

    public Map<String, String> getLabels() {
        return genericContainers.getFirst().getLabels();
    }

    public GenericContainerReplicaSet setLabels(Map<String, String> labels) {
        genericContainers.forEach(c -> c.setLabels(labels));

        return this;
    }

    public String getNetworkMode() {
        return genericContainers.getFirst().getNetworkMode();
    }

    public GenericContainerReplicaSet setNetworkMode(String networkMode) {
        genericContainers.forEach(c -> c.setNetworkMode(networkMode));

        return this;
    }

    public GenericContainerReplicaSet setNetwork(Network network) {
        genericContainers.forEach(c -> c.setNetwork(network));

        return this;
    }
}
