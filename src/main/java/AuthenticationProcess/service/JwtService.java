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

    // สร้าง JWT Token
    public String GenerateToken(String username){
        Map<String, Object> claims = new HashMap<>(); //จับคู่ "String" : "Value"
        return Jwts.builder() //เทธอทสร้าง Jwt
                .setClaims(claims) //เมธอทยัดข้อมูลลง Payload สามารถกำหนดเองได้
                .setSubject(username) // เมธอทกำหนดชื่อผู้สร้าง jwt
                .setIssuedAt(new Date(System.currentTimeMillis())) // ใส่เวลาที่ token ถูกสร้าง
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30)) // ใส่เวลาที่ token หมดอายุ
                .signWith(SignatureAlgorithm.HS256, getSignKey()) // ใส่ signature โดยใช้ secret key
                .compact(); // ทำการ compact token
    }

    // ดึงข้อมูลทั้งหมดจาก Token
    private Claims ExtractAllClaims(String token){
        return Jwts.parser()//สร้าง parser(ตัวแยกวิเคราะห์) สำหรับ JWT Token.
                .setSigningKey(getSignKey()) // กำหนดคีย์สำหรับการตรวจสอบลายเซ็นต์(signature)โดยใช้เมธอดget SignKey()
                .parseClaimsJws(token) //ทำการแปลง JWT Token ในรูปแบบของ JWS (JSON Web Signature)
                .getBody(); // ดึงข้อมูล claims ทั้งหมดจาก JWS.
    }

    // สร้าง Key สำหรับการ Sign และ Verify Token
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECERET);  // แปลง secret key จาก Base64 เป็น byte array
        return Keys.hmacShaKeyFor(keyBytes); // สร้าง Key จาก byte array โดยใช้ HMAC-SHA256
    }

    // ดึงข้อมูลจาก Token โดยใช้ claimResolver
    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
        // <T> T เป็น method ที่สามารถทำงานกับข้อมูลของประเภทใดก็ได้
        // Function<Claims, T> claimResolver คือ parameter ที่รับเป็นฟังก์ชัน
        // โดย Claims คือประเภทของข้อมูลที่ถูกใช้ใน JWT library ของ Java (ลักษณะคล้ายกับ Map ใน Java)
        // และ T คือประเภทข้อมูลที่จะถูก return หลังจากที่ฟังก์ชัน claimResolver ทำงาน
        // ดังนั้น, claimResolver คือฟังก์ชันที่รับ Claims เป็นอาร์กิวเมนต์เข้ามา
        // และ return ข้อมูลประเภท T ตาม signature ของ Function<Claims, T> นั้น.
        final Claims claims = ExtractAllClaims(token);
        return claimResolver.apply(claims); // เมธอท .apply คือการส่งค่า claims ที่อยู่ใน claimResolver ออกไป
    }

    // ดึงข้อมูล username จาก Token
    public String ExtractUsername(String token){
        return extractClaim(token,Claims::getSubject); // Claims::getSubject อ้างอิงถึง Subject ที่อยู่ใน token
    }

    // ดึงวันที่ Token หมดอายุ
    public Date ExtractExpiration(String token){
        return extractClaim(token,Claims::getExpiration); // Claims::getExpiration อ้างอิงถึง Expiration ที่อยู่ใน token
    }

    // ตรวจสอบว่า Token หมดอายุหรือไม่
    private Boolean IsTokenExpired(String token){
        return ExtractExpiration(token) // คืนค่าเป็นวันที่ Token หมดอายุ
                .before(new Date()); // คือวันที่ปัจจุบัน
        //จะเป็น true ถ้า Token หมดอายุ (วันหมดอายุน้อยกว่าปัจจุบัน), และ false ถ้า Token ยังไม่หมดอายุ
    }

    // ตรวจสอบความถูกต้องของ Token
    private Boolean ValidateToken(String token, UserDetails userDetails){
        //  ดึงชื่อผู้ใช้จาก Token โดยใช้ ExtractUsername ซึ่งดึงข้อมูลจาก Claims::getSubject ใน Token.
        final String userName = ExtractUsername(token);
        // userName.equals(userDetails.getUsername()) เปรียบเทียบชื่อผู้ใช้จาก Token กับชื่อผู้ใช้ใน UserDetails ที่ถูกส่งเข้ามา
        // !IsTokenExpired(token) ตรวจสอบว่า Token ยังไม่หมดอายุ
        return (userName.equals(userDetails.getUsername()) && !IsTokenExpired(token));
    }
}
