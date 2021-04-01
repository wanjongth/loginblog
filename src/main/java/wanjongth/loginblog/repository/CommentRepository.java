package wanjongth.loginblog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import wanjongth.loginblog.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> , JpaSpecificationExecutor<Comment> {
    List<Comment> findAllByOrderByModifiedAtDesc();
}