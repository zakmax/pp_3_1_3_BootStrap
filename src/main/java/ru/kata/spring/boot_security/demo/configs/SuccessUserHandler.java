package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("=== SUCCESS HANDLER ===");
        System.out.println("Redirecting to /admin");

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("admin") || role.equals("ADMIN"));

        System.out.println("Is admin: " + isAdmin);

        if (isAdmin) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/user");
        }
    }
}


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {




//       System.out.println("=== AUTHENTICATION SUCCESS ===");
//        System.out.println("Username: " + authentication.getName());
//        System.out.println("Authorities: " + authentication.getAuthorities());
//
//        // Проверяем роли - учитываем разные варианты написания
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .anyMatch(role -> role.equals("admin") ||
//                        role.equals("ADMIN") ||
//                        role.equals("ROLE_admin") ||
//                        role.equals("ROLE_ADMIN"));
//
//        System.out.println("Is admin: " + isAdmin);
//        System.out.println("==============================");
//
//        if (isAdmin) {
//            response.sendRedirect("/admin");
//        } else {
//            response.sendRedirect("/user");
//        }
//    }
//}



//        if (authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .anyMatch(v->v.equals("admin"))) {
//            httpServletResponse.sendRedirect("/admin");
//        } else {
//            httpServletResponse.sendRedirect("/user");
//        }
//    }
//}