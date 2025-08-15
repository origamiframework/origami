package ru.origami.common.task;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.origami.common.utils.AllureAttachment;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

/**
 * Задача, которая возвращает результат из отдельного потока.
 * Может вызвать исключение.
 *
 * <p>Для использования необходимо:
 * <p>1. Унаследоваться от класса {@link Task}
 * <p>2. Переопределить конструктор с вызовом конструктора родителя
 * <pre>{@code
 *     public ServiceMethodTask(String methodName, Object... args) {
 *         super(methodName, args);
 *     }}</pre>
 * <p>3. Реализовать необходимые для задачи методы - с возвращаемым результатом или без
 * <p>4. В тестовом методе создать новый объект, где в сигнатуре конструктора:<ul>
 *     <li> первый параметр <i>methodName</i> - название метода для запуска в отдельном потоке
 *     <li> последующие параметры (могут быть/могут не быть) - входящие параметры для указанного метода
 *       <p><b>Очередность параметров должна строго соответствовать очередности метода</b>
 * </ul>
 * <p>5. Вызвать у объекта {@link #submit()} для запуска задачи в отдельном потоке
 * <p>6. Вызвать один из нижеперечисленных методов:<ul>
 *     <li> {@link #get()} / {@link #get(long, TimeUnit)} - при необходимости ожидает завершения задачи, а затем извлекает результат
 *     <li> {@link #cancel(boolean)} - пытается завершить выполнение текущей задачи
 *     <li> {@link #isCancelled()} - возвращает {@code true}, если эта задача была отменена до ее завершения
 *     <li> {@link #isDone()} - возвращает {@code true}, если эта задача завершена
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Task implements Callable<Object> {

    /**
     * @param methodName название реализованного метода для запуска в отдельном потоке
     * @param args входящие параметры для указанного метода в той же последовательности, что и в методе (могут быть/могут не быть)
     */
    public Task(String methodName, Object... args) {
        this.methodName = methodName;

        Arrays.asList(args).forEach(this::addParameter);
    }

    protected String methodName;

    private Class[] parameterTypes = null;

    private Object[] parameterValues = null;

    private Future<Object> future;

    private void addParameter(Object value) {
        getParameterTypes()[parameterTypes.length - 1] = value.getClass();
        getParameterValues()[parameterValues.length - 1] = value;
    }

    private Class[] getParameterTypes() {
        if (Objects.isNull(parameterTypes)) {
            parameterTypes = new Class[1];

            return parameterTypes;
        }

        parameterTypes = Arrays.copyOf(parameterTypes, parameterTypes.length + 1);

        return parameterTypes;
    }

    private Object[] getParameterValues() {
        if (Objects.isNull(parameterValues)) {
            parameterValues = new Object[1];

            return parameterValues;
        }

        parameterValues = Arrays.copyOf(parameterValues, parameterValues.length + 1);

        return parameterValues;
    }

    /**
     *
     * @return результат выполнения methodName
     */
    @Override
    public final Object call() throws Exception {
        Objects.requireNonNull(methodName, getLangValue("method.name.empty.error"));
        Method method;

        try {
            method = this.getClass().getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ex) {
            String typeValues = "";

            if (Objects.nonNull(parameterTypes)) {
                typeValues = String.format(" с параметрами \"%s\"",
                        Arrays.stream(parameterTypes).map(Class::toString).collect(Collectors.joining(", ")));
            }

            throw new NoSuchMethodException(getLangValue("no.such.method.error").formatted(methodName, typeValues));
        }

        method.setAccessible(true);

        return method.invoke(this, parameterValues);
    }

    /**
     * Запускает задачу в отдельном потоке.
     *
     * @return текущий объект
     */
    public Task submit() {
        try {
            this.future = Executors.newFixedThreadPool(2).submit(this);
        } catch (Exception e) {
            failByException(e);
        }

        return this;
    }

    /**
     * При необходимости ожидает завершения задачи, а затем извлекает результат, если он доступен.
     *
     * @return полученный результат
     * @throws CancellationException если вычисление было отменено
     * @throws ExecutionException если вычисление вызвало исключение
     * @throws InterruptedException если текущий поток был прерван во время ожидания
     */
    public <R> R get() {
        try {
            return (R) future.get();
        } catch (Exception e) {
            failByException(e);
        }

        return null;
    }

    /**
     * При необходимости ожидает завершения задачи, но не более заданного времени,
     * а затем извлекает результат, если он доступен.
     *
     * @return полученный результат
     * @throws CancellationException если вычисление было отменено
     * @throws ExecutionException если вычисление вызвало исключение
     * @throws InterruptedException если текущий поток был прерван во время ожидания
     */
    public <R> R get(long timeout, TimeUnit unit) {
        try {
            return (R) future.get(timeout, unit);
        } catch (Exception e) {
            failByException(e);
        }

        return null;
    }

    /**
     * Пытается завершить выполнение текущей задачи.
     *
     * <p>Может быть <b>сбой</b> во время попытки, если задача уже выполнена, отменена
     * или не может быть отменена по какой-либо другой причине.
     * <p>В случае <b>успеха</b> и если задача не запущена при вызове {@code #cancel()},
     * эта задача никогда не должна выполниться. Если задача уже запущена, тогда параметр {@code mayInterruptIfRunning}
     * определяет должен ли поток, выполняющий эту задачу, быть прерван в попытках остановить задачу.
     *
     * <p>После выполнения этого метода последующие вызовы {@link #isDone()} будут всегда возвращать {@code true}.
     * Последующие вызовы {@link #isCancelled()} всегда будет возвращать {@code true}, если этот метод вернул {@code true}.
     *
     * @param mayInterruptIfRunning {@code true}, если поток, выполняющий задание должно быть прерван;
     * иначе выполняемые задачи должны быть завершены
     * @return {@code false}, если задачу нельзя было отменить, обычно потому, что задача уже завершена нормально;
     * {@code true} в противном случае
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            return future.cancel(mayInterruptIfRunning);
        } catch (Exception e) {
            failByException(e);
        }

        return false;
    }

    /**
     *
     * @return {@code true}, если эта задача была отменена до ее завершения
     */
    public boolean isCancelled() {
        try {
            return future.isCancelled();
        } catch (Exception e) {
            failByException(e);
        }

        return false;
    }

    /**
     * Возвращает {@code true}, если эта задача завершена.
     * Завершение может быть связано с обычным завершением, исключением или отменой — во всех этих случаях
     * этот метод вернет значение {@code true}.
     *
     * @return {@code true}, если эта задача завершена
     */
    public boolean isDone() {
        try {
            return future.isDone();
        } catch (Exception e) {
            failByException(e);
        }

        return false;
    }

    private void failByException(Exception e) {
        Throwable targetException = e.getCause().getCause();

        if (Objects.isNull(targetException)) {
            targetException = e.getCause();
        }

        AllureAttachment.attachFailMethodToAllure(methodName, Arrays.toString(targetException.getStackTrace()));

        fail(getLangValue("method.called.error").formatted(methodName, targetException.getMessage()));
    }

    @Override
    public String toString() {
        return String.format("Method: %s, params: %s", methodName, Arrays.toString(parameterValues));
    }
}
