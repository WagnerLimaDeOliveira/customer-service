package com.insurance.customer_service.controller;

import com.insurance.customer_service.entity.Customer;
import com.insurance.customer_service.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/policy")
public class PolicyController {
    @Inject
    CustomerService customerService;

    @GET
    @Path("{policyNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomerByPolicyNumber(@PathParam("policyNumber") UUID policyNumber) {
        Customer customer = customerService.getCustomerByPolicyNumber(policyNumber);
        if (customer == null) {
            throw new WebApplicationException("Customer not found", Response.Status.NOT_FOUND);
        } else {
            return customer;
        }
    }
}
