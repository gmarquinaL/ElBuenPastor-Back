package BP.application.service.impl;

import BP.domain.dao.IChildrenRepo;
import BP.domain.dao.IGenericRepo;
import BP.domain.entity.Children;
import BP.application.service.IChildrenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildrenServiceImpl extends CRUDImpl<Children, Integer> implements IChildrenService
{

    private final IChildrenRepo repo;

    @Override
    protected IGenericRepo<Children, Integer> getRepo() {
        return repo;
    }
}
