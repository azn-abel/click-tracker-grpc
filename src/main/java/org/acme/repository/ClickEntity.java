package org.acme.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "click")
public class ClickEntity extends PanacheEntity {
    public String host;
    public double x;
    public double y;
    public String timestamp;
    public String userId;
}