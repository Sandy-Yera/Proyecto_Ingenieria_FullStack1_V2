package com.logistica.ms_workorders.client;

import com.logistica.ms_workorders.dto.feign.UserFeignDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-users", path = "/api/users")
public interface UsersClient {

    @GetMapping("/{id}")
    UserFeignDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}