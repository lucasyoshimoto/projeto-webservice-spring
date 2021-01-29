package com.lucas.soap.webservices.customeradministration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucas.soap.webservices.customeradministration.bean.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

}