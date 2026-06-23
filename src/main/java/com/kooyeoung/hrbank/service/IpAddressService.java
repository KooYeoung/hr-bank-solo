package com.kooyeoung.hrbank.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class IpAddressService {

    private final ObjectProvider<HttpServletRequest> requestObjectProvider;

    public String getClientIp() {
        HttpServletRequest request = requestObjectProvider.getObject();

        String ip = request.getHeader("X-Forwarded-For");

        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");

        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }


}
