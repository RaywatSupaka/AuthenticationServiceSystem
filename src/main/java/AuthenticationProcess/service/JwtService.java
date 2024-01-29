package AuthenticationProcess.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;


public class JwtService {

    private static final String SECERET = "ASDJAHSIJH12USDFGLSDJFHGOUESHRGRGFHFGHFGJES0IOGESRIOGSGFSGHOI@!@^&#%!@$@!$!@JKH";

    public String GenerateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
                .signWith(SignatureAlgorithm.HS256, getSignKey()).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECERET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String ExtractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date ExtractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
        final Claims claims = ExtractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims ExtractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean IsTokenExpired(String token){
        return ExtractExpiration(token)
                .before(new Date());
    }

    private Boolean ValidateToken(String token, UserDetails userDetails){
        final String userName = ExtractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !IsTokenExpired(token));
    }
}
