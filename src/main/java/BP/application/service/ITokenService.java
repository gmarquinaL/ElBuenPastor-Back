package BP.application.service;

import BP.domain.entity.Token;
import BP.domain.entity.User;

public interface ITokenService
{
    void saveToken(User user, String jwtToken); //opcional

    void revokeAllTokensByUser(User user);
    Token findByToken(String token);

    void deleteByUserIdAndToken(Integer idUser, String token);
}
