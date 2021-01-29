package com.lucas.soap.webservices.customeradministration.soap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.lucas.CustomerDetail;
import com.lucas.DeleteCustomerRequest;
import com.lucas.DeleteCustomerResponse;
import com.lucas.GetAllCustomerDetailRequest;
import com.lucas.GetAllCustomerDetailResponse;
import com.lucas.GetCustomerDetailRequest;
import com.lucas.GetCustomerDetailResponse;
import com.lucas.InsertCustomerRequest;
import com.lucas.InsertCustomerResponse;
import com.lucas.soap.webservices.customeradministration.bean.Customer;
import com.lucas.soap.webservices.customeradministration.service.CustomerDetailService;
import com.lucas.soap.webservices.customeradministration.soap.exception.CustomerNotFoundException;

@Endpoint
public class CustomerDetailEndPoint {

	@Autowired
	CustomerDetailService service;
	
	@PayloadRoot(namespace="http://lucas.com", localPart="GetCustomerDetailRequest")
	@ResponsePayload
	public GetCustomerDetailResponse processCustomerDetailRequest(@RequestPayload GetCustomerDetailRequest req) throws Exception {
		Customer customer = service.findById(req.getId());
		if(customer == null) {
			throw new CustomerNotFoundException("Invalid Customer id "+req.getId());
		}
		return convertToCustomerDetailResponse(customer);
	}
	
	private GetCustomerDetailResponse convertToCustomerDetailResponse(Customer customer) {
		GetCustomerDetailResponse resp = new GetCustomerDetailResponse();
		resp.setCustomerDetail(convertToCustomerDetail(customer));
		return resp;
	}
	
	private CustomerDetail convertToCustomerDetail(Customer customer) {
		CustomerDetail customerDetail = new CustomerDetail();
		customerDetail.setId(customer.getId());
		customerDetail.setName(customer.getName());
		customerDetail.setPhone(customer.getPhone());
		customerDetail.setEmail(customer.getEmail());
		return customerDetail;
	}
	
	@PayloadRoot(namespace="http://lucas.com", localPart="GetAllCustomerDetailRequest")
	@ResponsePayload
	public GetAllCustomerDetailResponse processGetAllCustomerDetailResponse(@RequestPayload GetAllCustomerDetailRequest req) {
		List<Customer> customers = service.findAll();
		return convertToGetAllCustomerDetailResponse(customers);
	}
	
	private GetAllCustomerDetailResponse convertToGetAllCustomerDetailResponse(List<Customer> customers) {
		GetAllCustomerDetailResponse resp = new GetAllCustomerDetailResponse();
		customers.stream().forEach(c -> resp.getCustomerDetail().add(convertToCustomerDetail(c)));
		return resp;
	}
	
	@PayloadRoot(namespace="http://lucas.com", localPart="DeleteCustomerRequest")
	@ResponsePayload
	public DeleteCustomerResponse deleteCustomerRequest(@RequestPayload DeleteCustomerRequest req) {
		DeleteCustomerResponse resp = new DeleteCustomerResponse();
		resp.setStatus(convertStatusSoap(service.deleteById(req.getId())));
		return resp;
	}
	
	private com.lucas.Status convertStatusSoap(com.lucas.soap.webservices.customeradministration.helper.Status statusService) {
		if(statusService == com.lucas.soap.webservices.customeradministration.helper.Status.FAILURE) {
			return com.lucas.Status.FAILURE;
		}
		return com.lucas.Status.SUCCESS;
	}
	
	@PayloadRoot(namespace="http://lucas.com", localPart="InsertCustomerRequest")
	@ResponsePayload
	public InsertCustomerResponse insertCustomerRequest(@RequestPayload InsertCustomerRequest req) {
		InsertCustomerResponse resp = new InsertCustomerResponse();
		resp.setStatus(convertStatusSoap(service.insert(convertCustomer(req.getCustomerDetail()))));
		return resp;
	}
	
	private Customer convertCustomer(CustomerDetail customerDetail) {
		return new Customer(customerDetail.getId(),customerDetail.getName(),customerDetail.getPhone(),customerDetail.getEmail());
	}
	
}
