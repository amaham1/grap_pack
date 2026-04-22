package co.grap.pack.admin.auth.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 통합 운영 포털 세션 사용자 정보다.
 */
@Getter
@Builder
public class AdminSessionPrincipal implements UserDetails {

    private final Long id;
    private final String loginId;
    private final String password;
    private final String name;
    private final String email;
    private final AdminOperatorRole role;
    private final Boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }
}
