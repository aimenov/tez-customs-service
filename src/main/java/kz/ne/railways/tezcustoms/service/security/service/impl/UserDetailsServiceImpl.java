package kz.ne.railways.tezcustoms.service.security.service.impl;

import kz.ne.railways.tezcustoms.service.entity.User;
import kz.ne.railways.tezcustoms.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername " + email);
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        //
        // BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // User user = new User();
        // user.setPassword(passwordEncoder.encode("12345"));
        // user.setId(1L);
        // user.setEmail(email);
        return UserDetailsImpl.build(user);
    }
}

