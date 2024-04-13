package com.group2.catanbackend.service;

import com.group2.catanbackend.exception.ErrorCode;
import com.group2.catanbackend.exception.NotImplementedException;
import com.group2.catanbackend.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class RunningInstanceService {
    private String gameId;
    private List<Player> players;
    private final MessagingService messagingService;
    //TODO: Game Board

    @Autowired
    public RunningInstanceService(MessagingService messagingService){
        this.messagingService = messagingService;
    }

    public void makeMove(Object gameMove)  {
        //TODO: Implement
        throw new NotImplementedException(ErrorCode.ERROR_NOT_IMPLEMENTED);
    }
}
