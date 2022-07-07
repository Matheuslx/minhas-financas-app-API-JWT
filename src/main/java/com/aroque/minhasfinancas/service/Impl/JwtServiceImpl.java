package com.aroque.minhasfinancas.service.Impl;

import com.aroque.minhasfinancas.model.UsuarioModel;
import com.aroque.minhasfinancas.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    // Esses values estão no application.properties
    @Value("${jwt.expiracao}")
    private String expiracao;

    @Value("jwt.chave-assinatura")
    private String chaveAssinatura;


    @Override
    public String gerarToken(UsuarioModel usuario) {
        // No application.properties a data de expiração foi definida como 30 minutos
        long exp = Long.valueOf(expiracao);
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(exp);
        // Transformando local date time em date
        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);

        String horaExpiracaoToken = dataHoraExpiracao.toLocalTime()
                                                    .format(DateTimeFormatter.ofPattern("HH:mm"));


        String token = Jwts.builder()
                            .setExpiration(data)
                            .setSubject(usuario.getEmail())
                            .claim("userid", usuario.getId())
                            .claim("nome", usuario.getNome())
                            .claim("horaExpiracao", horaExpiracaoToken)
                            .signWith(SignatureAlgorithm.HS512, chaveAssinatura)
                            .compact();
        return token;
    }

    @Override
    public Claims obterClaims(String token) throws ExpiredJwtException {
        // Claims são as informações contidas no token
        return Jwts.parser().setSigningKey(chaveAssinatura).parseClaimsJws(token).getBody();
    }

    @Override
    public boolean isTokenValido(String token) {
        try{
            Claims claims = obterClaims(token);
            java.util.Date dataExp = claims.getExpiration();

            // Transformando em local date time
            LocalDateTime dataExpiracao = dataExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // A data hora atual não é depois da expiração
            boolean dataHoraAtualIsAfterDataExpiracao = LocalDateTime.now().isAfter(dataExpiracao);
            return !dataHoraAtualIsAfterDataExpiracao;

        } catch (ExpiredJwtException e ){
            return false;
        }

    }

    @Override
    public String obterLoginUsuario(String token) {
        Claims claims = obterClaims(token);
        return claims.getSubject();
    }
}
