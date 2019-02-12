//package com.propra.happybay.Service;
//
//import com.propra.happybay.Model.Person;
//import com.propra.happybay.Repository.PersonRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.Optional;
//
//public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
//
//    @Autowired
//    private PersonRepository personRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<Person> userOptional = personRepository.findByUsername(username);
//        if (userOptional.isPresent()) {
//            Person user = userOptional.get();
//            UserDetails userDetails = User.builder()
//                    .username(user.getUsername())
//                    .password(user.getPassword())
//                    .authorities(user.getRole())
//                    .build();
//            return userDetails;
//        }
//        throw new UsernameNotFoundException("Invalid username");
//    }
//}
