package BP.application.service.impl;

import BP.application.exception.ModelNotFoundException;
import BP.application.service.ITokenService;
import BP.domain.dao.ITokenRepo;
import BP.domain.entity.Token;
import BP.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static BP.application.util.ConstantUtil.TOKEN_BEARER;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements ITokenService
{
    private final ITokenRepo repo;

    @Override
    public void saveToken(User user, String jwtToken) {
        System.out.println("ID: " + user.getId());
        Token token = Token.builder()
                .user(user)
                .dateGenerated(LocalDateTime.now())
                .token(jwtToken)
                .type(TOKEN_BEARER)
                .expired(false)
                .revoked(false)
                .build();
        repo.save(token);
    }

    @Override
    public void revokeAllTokensByUser(User user) {
        List<Token> validUserTokens = repo.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }

        repo.deleteAllById(
                validUserTokens.stream()
                        .map(Token::getId)
                        .toList()
        );
    }

    @Override
    public Token findByToken(String token) {
        Token tokenFound = repo.findByToken(token);

        if(tokenFound == null){
            throw new ModelNotFoundException("Token no encontrado");
        }
        return tokenFound;
    }

    @Transactional
    @Override
    public void deleteByUserIdAndToken(Integer id, String token) {
        repo.deleteByUserIdAndToken(id, token);
    }
}
