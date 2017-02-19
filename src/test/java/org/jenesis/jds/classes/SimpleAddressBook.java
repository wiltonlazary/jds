package org.jenesis.jds.classes;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jenesis.jds.JdsEntity;
import org.jenesis.jds.annotations.JdsEntityAnnotation;

import java.util.List;

@JdsEntityAnnotation(entityId = 2, entityName = "Simple Address Book")
public class SimpleAddressBook extends JdsEntity {
    private final SimpleListProperty<SimpleAddress> addresses;

    public SimpleAddressBook() {
        this.addresses = new SimpleListProperty<>(FXCollections.observableArrayList());
        //map your objects
        map(SimpleAddress.class, addresses);
    }

    public List<SimpleAddress> getAddresses() {
        return this.addresses.get();
    }

    public void SetAlternativeAddresses(List<SimpleAddress> value) {
        this.addresses.set((ObservableList<SimpleAddress>) value);
    }

    @Override
    public String toString() {
        return "SimpleAddressBook{" +
                "addresses = " + addresses.get() +
                '}';
    }
}