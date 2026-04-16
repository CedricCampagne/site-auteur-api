package com.cedric.site_auteur_api.service;

import org.springframework.stereotype.Service;

import com.cedric.site_auteur_api.dto.user.UserCreateDto;
import com.cedric.site_auteur_api.dto.user.UserDto;
import com.cedric.site_auteur_api.dto.user.UserUpdateDto;
import com.cedric.site_auteur_api.dto.user.UserFullDto;

import com.cedric.site_auteur_api.entity.User;
import com.cedric.site_auteur_api.mapper.UserMapper;
import com.cedric.site_auteur_api.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    
    //injecte le repository
    private final UserRepository userRepository;

    //Constructeur
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    // Create
    public UserFullDto createUser(UserCreateDto dto) {
        User user = new User();

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password()); // plus tard → BCrypt
        user.setIsActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        User saved = userRepository.save(user);

        return UserMapper.toFullDto(saved);
    }

    //Update
    public UserFullDto updateUser(Integer id,UserUpdateDto dto) {
        User user = userRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Utilisateur introuvable avec l'id : " + id));
        
        user.setUsername(dto.username());
        user.setEmail(dto.email()); 
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

    //Toggle status
    public UserFullDto toggleUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable avec l'id : " + id));

        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(OffsetDateTime.now());

        User updated = userRepository.save(user);

        return UserMapper.toFullDto(updated);
    }
}
