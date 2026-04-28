package com.cedric.site_auteur_api.service;

import com.cedric.site_auteur_api.entity.Chronicle;
import com.cedric.site_auteur_api.entity.Comment;
import com.cedric.site_auteur_api.entity.User;
import com.cedric.site_auteur_api.mapper.CommentMapper;
import com.cedric.site_auteur_api.repository.ChronicleRepository;
import com.cedric.site_auteur_api.repository.CommentRepository;
import com.cedric.site_auteur_api.repository.UserRepository;
import com.cedric.site_auteur_api.dto.Comment.CommentCreateDto;
import com.cedric.site_auteur_api.dto.Comment.CommentFullDto;
import com.cedric.site_auteur_api.dto.Comment.CommentUpdateDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

@Service
public class CommentService {

    // Injection du repository
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ChronicleRepository chronicleRepository;

    //Constructeur
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, ChronicleRepository chronicleRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.chronicleRepository = chronicleRepository;
        
    }

    // les Méthodes

    //All comments
    public List<CommentFullDto>getAllComment() {
        return commentRepository.findAll()
            .stream()
            .map(CommentMapper::toFullDto)
            .toList();
    }

    // comment by id
    public CommentFullDto getCommentById( Integer id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avec l'id : " + id));
        
        return CommentMapper.toFullDto(comment);
    }

    //Comments par id de chronique
    public List<CommentFullDto> getCommentByIdChronicle(Integer idChronicle){
        List<Comment> comments = commentRepository.findByChronicleIdChronicle(idChronicle);

        return comments.stream()
            .sorted((c1, c2)-> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
            .map(CommentMapper::toFullDto)
            .toList();
    }

    //delete
    public void deleteCommentbyId( Integer id ) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avec l 'id : " + id));
    
            commentRepository.delete(comment);
    }

    //create
    public CommentFullDto createComment( CommentCreateDto dto ) {

        //charger le user coresspondant pour pouvoir faire comment.setUser(user);
        User user = userRepository.findById(dto.idUser())
        .orElseThrow(() -> new NoSuchElementException("Utilisateur introuvable  "));

        //charger le user coresspondant pour pouvoir faire comment.setUser(user);
        Chronicle chronicle = chronicleRepository.findById(dto.idChronicle())
        .orElseThrow(() -> new NoSuchElementException("chronique introuvable"));

        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setIsVisible(dto.isVisible());
        comment.setUser(user);
        comment.setChronicle(chronicle);
        comment.setCreatedAt(OffsetDateTime.now());
        comment.setUpdatedAt(OffsetDateTime.now());

        Comment saved = commentRepository.save(comment);

        return CommentMapper.toFullDto(saved);
    }

    //update
    public CommentFullDto updateComment( Integer id, CommentUpdateDto dto ) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avec l'id : " + id));

        comment.setContent(dto.content());
        comment.setIsVisible(dto.isVisble());
        comment.setUpdatedAt(OffsetDateTime.now());

        Comment updated = commentRepository.save(comment);

        return CommentMapper.toFullDto(updated);
    }

    //toggle de status
    public CommentFullDto toggleCommentStatus(Integer id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avecl 'id : " + id));

        comment.setIsVisible(!comment.getIsVisible());
        comment.setUpdatedAt(OffsetDateTime.now());

        Comment updated = commentRepository.save(comment);

        return CommentMapper.toFullDto(updated);
    }
}
