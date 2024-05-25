package BP.application.service.impl;

import BP.domain.dao.IGenericRepo;
import BP.application.service.ICRUD;

import java.io.Serializable;
import java.util.List;

public abstract class CRUDImpl<T, ID extends Serializable> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public T save(T entity) throws Exception {
        return getRepo().save(entity);
    }

    @Override
    public T update(T entity, ID id) throws Exception {
        return getRepo().save(entity);
    }

    @Override
    public T readById(ID id) throws Exception {
        return getRepo().findById(id).orElse(null);
    }

    @Override
    public List<T> readAll() throws Exception {
        return getRepo().findAll();
    }

    @Override
    public void deleteById(ID id) throws Exception {
        getRepo().deleteById(id);
    }
}
