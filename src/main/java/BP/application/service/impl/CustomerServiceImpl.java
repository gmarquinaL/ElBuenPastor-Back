package BP.application.service.impl;

import BP.application.service.ICustomerService;
import BP.domain.dao.ICustomerRepo;
import BP.domain.dao.IGenericRepo;
import BP.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends CRUDImpl<Customer, Integer> implements ICustomerService
{

    private final ICustomerRepo repo;

    @Override
    protected IGenericRepo<Customer, Integer> getRepo() {
        return repo;
    }
}
