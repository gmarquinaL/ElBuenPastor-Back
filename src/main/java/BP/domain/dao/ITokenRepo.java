package BP.domain.dao;

import BP.domain.entity.Token;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITokenRepo extends IGenericRepo<Token, Integer>
{
    @Query(value = """
      select t from Token t inner join User u
      on t.user.id = u.id
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Integer id);

    Token findByToken(String token);
    /*@Modifying
    @Query("""
    delete from Token t where t.user.id = :idUser and t.token = :token
    """)
    void deleteTokenByUser(Integer idUser, String token);*/

    void deleteByUserIdAndToken(Integer id, String token);
}
