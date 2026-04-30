package com.cedric.site_auteur_api.service.admin;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cedric.site_auteur_api.entity.User;
import com.cedric.site_auteur_api.dto.user.AdminUserUpdateDto;
import com.cedric.site_auteur_api.dto.user.AdminUserCreateDto;
import com.cedric.site_auteur_api.dto.user.UserFullDto;
import com.cedric.site_auteur_api.mapper.UserMapper;
import com.cedric.site_auteur_api.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    //All
    public List<UserFullDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(UserMapper::toFullDto)
            .toList();
    }

    // by id
    public UserFullDto getUserById(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Utilisateur non trouvable avecl 'id : " + id));

        return UserMapper.toFullDto(user);
    }
    
    //Update
    public UserFullDto updateUser(Integer id,AdminUserUpdateDto dto) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Utilisateur introuvable avec l'id : " + id));
        
        if(dto.username() != null){
            user.setUsername(dto.username());            
        }
        if(dto.email() != null){
            user.setEmail(dto.email()); 
        }
        if(dto.isActive() != null){
            user.setIsActive(dto.isActive());
        }
        if(dto.password() != null && !dto.password().isBlank()){
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        user.setUpdatedAt(OffsetDateTime.now());

        User updated = userRepository.save(user);

        return UserMapper.toFullDto(updated);
    }

    //Toggle status
    public UserFullDto toggleUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable avec l'id : " + id));

        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(OffsetDateTime.now());

        User updated = userRepository.save(user);

        return UserMapper.toFullDto(updated);
    }

    //Delete
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Utilisateur introuvable avec l 'id : " + id));

        userRepository.delete(user);
    }

    // Create
    public UserFullDto createUser(AdminUserCreateDto dto) {
        User user = new User();

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setIsActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        User saved = userRepository.save(user);

        return UserMapper.toFullDto(saved);
    }
}
