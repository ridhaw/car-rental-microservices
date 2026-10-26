package com.ecommerce.authservice.feign;

import com.ecommerce.authservice.dto.CustomerRequest;
import com.ecommerce.authservice.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rental-service")
public interface CustomerFeignClient {

    @PostMapping("/api/customers")
    CustomerResponse createCustomer(@RequestBody CustomerRequest request);
}
