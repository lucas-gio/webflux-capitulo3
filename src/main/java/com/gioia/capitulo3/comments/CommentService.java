package com.gioia.capitulo3.comments;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
	private final CommentWriterRepository commentWriterRepository;

	public CommentService(CommentWriterRepository commentWriterRepository){
		this.commentWriterRepository = commentWriterRepository;
	}

	@RabbitListener(bindings = @QueueBinding(
		value = @Queue,
		exchange = @Exchange(value = "libro-webflux"),
		key = "comments.new"))
	public void save(Comment comment){
		commentWriterRepository
			.save(comment)
			.log("commentService.save")
			.subscribe();
	}

}
