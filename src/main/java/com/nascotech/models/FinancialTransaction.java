/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nascotech.models;

import java.math.BigDecimal;

/**
 *
 * @author User
 */
public class FinancialTransaction {
    private String Id;
    private String Status;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "FinancialTransaction{" +
                "Id='" + Id + '\'' +
                ", Status='" + Status + '\'' +
                '}';
    }
}
