package moe.plushie.armourers_workshop.builder.data.properties;

import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DataProperty<T> {

    protected T value;
    protected Consumer<Boolean> editingObserver;
    protected final ArrayList<Consumer<T>> valueObservers = new ArrayList<>();

    public DataProperty() {
    }

    public DataProperty(T value) {
        this.value = value;
    }

    public void beginEditing() {
        if (editingObserver != null) {
            editingObserver.accept(true);
        }
    }

    public void endEditing() {
        if (editingObserver != null) {
            editingObserver.accept(false);
        }
    }

    public void set(T value) {
        if (Objects.equals(this.value, value)) {
            return;
        }
        this.value = value;
        this.valueObservers.forEach(it -> it.accept(value));
    }

    public T get() {
        return value;
    }

    public T getOrDefault(T defaultValue) {
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public void addObserver(Consumer<T> observer) {
        this.valueObservers.add(observer);
    }

    public void addEditingObserver(Consumer<Boolean> observer) {
        this.editingObserver = observer;
    }
}
