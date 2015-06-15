package mine.daphne.service;

import java.util.List;

import mine.daphne.model.entity.ScrumBacklog;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("scrumService")
@Transactional
public class ScrumService extends BaseService {

	public ScrumBacklog getBacklogByAssignee(String assignee) {
		List<ScrumBacklog> backlog = query("from ScrumBacklog where assignee = ? order by timestamp desc", assignee);
		if (backlog.size() == 0) {
			return null;
		} else {
			return backlog.get(0);
		}
	}

	public void saveBacklog(ScrumBacklog backlog) {
		deleteAll(query("from ScrumStory where backlog.id = ?", backlog.getId()));
		saveOrUpdate(backlog);
	}
}
