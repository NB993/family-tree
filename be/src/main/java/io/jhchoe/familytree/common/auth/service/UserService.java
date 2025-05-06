package io.jhchoe.familytree.common.auth.service;

import io.jhchoe.familytree.common.auth.UserJpaEntity;
import io.jhchoe.familytree.common.auth.UserJpaRepository;
import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.exception.FTException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public FTUser loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserJpaEntity userEntity = userJpaRepository.findByEmail(username)
//            .orElseThrow(() -> FTException.NOT_FOUND);
//
//        return FTUser.ofFormLoginUser(
//            userEntity.getId(),
//            userEntity.getName(),
//            userEntity.getEmail(),
//            userEntity.getPassword(),
//            userEntity.getEmail()
//        );
        return null;
    }

    @Transactional
    public void createUser(String username, String password) {
//        password = passwordEncoder.encode(password);
//        UserJpaEntity newUser = UserJpaEntity.ofFormLoginUser(username, password);
//        userJpaRepository.save(newUser);
    }
}
