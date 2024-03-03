package ru.light.statements.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;



@Component
@FeignClient(name = "dadata-client", url = "https://cleaner.dadata.ru/api/v1")
public interface DadataAPI {

    // я правда хотел вынести это в конфиги, но потом вспомнил, что все равно в гит выгружать (локально сохранять в конфиги - неудобно проверять) 
    public static final String dadataToken = "Token 31f00a4cf553cc43ac8b7596b40ed8c2e641b67c";
    public static final String dadataSecret = "b960b9842e9e669ded6d01231a05b73634c1ec97";

    @PostMapping(value = "/clean/phone", consumes = "application/json")
    String getPhoneData(@RequestHeader("Authorization") String token, 
                        @RequestHeader("X-Secret") String secretToken,
                        @RequestBody String phone);
    
}
