package com.seth.draft_assistant.service;

import com.seth.draft_assistant.model.internal.requests.ScoringSettingUpdateRequest;
import com.seth.draft_assistant.model.internal.scoring.ScoringSetting;
import com.seth.draft_assistant.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public List<ScoringSetting> getScoringSettings(){
        return scoreRepository.getScoringSettings();
    }

    public void updateScoringSettings(List<ScoringSettingUpdateRequest> request){
        scoreRepository.updateScoringSettings(request);
    }
}
