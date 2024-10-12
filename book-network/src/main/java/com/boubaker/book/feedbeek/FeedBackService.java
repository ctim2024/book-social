package com.boubaker.book.feedbeek;

import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.boubaker.book.book.Book;
import com.boubaker.book.book.BookRepository;
import com.boubaker.book.common.PageResponse;
import com.boubaker.book.exception.OperationNotPermittedException;
import com.boubaker.book.user.User;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final FeedBackRepository feedBackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

     
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
       
        Book book = bookRepository.findById(request.bookId())
                    .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));

       if (book.isArchived() || !book.isShareable()) {
                throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
            }
    
            User user = ((User) connectedUser.getPrincipal());
    
            if (Objects.equals(book.getOwner().getId(), user.getId())) {
                throw new OperationNotPermittedException("You cannot give feedback to your own book");
            }    
            
            
            Feedback feedback = feedbackMapper.toFeedback(request);
            return feedBackRepository.save(feedback).getId();
    }


    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }

}