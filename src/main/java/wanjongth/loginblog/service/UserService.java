package wanjongth.loginblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wanjongth.loginblog.dto.SignupRequestDto;
import wanjongth.loginblog.model.User;
import wanjongth.loginblog.repository.UserRepository;
import wanjongth.loginblog.security.UserDetailsImpl;
import wanjongth.loginblog.security.kakao.KakaoOAuth2;
import wanjongth.loginblog.security.kakao.KakaoUserInfo;
import wanjongth.loginblog.util.Validator;

import java.util.Optional;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KakaoOAuth2 kakaoOAuth2;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, KakaoOAuth2 kakaoOAuth2, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kakaoOAuth2 = kakaoOAuth2;
    }


    public void registerUser(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        // 회원 ID 중복 확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("duplicate");
        }

//        // ID 정규식 검사
//        if(!Validator.idValid(username)){
//            throw new IllegalArgumentException("idValid");
//        }

        // pw
        String password = requestDto.getPassword();
        String password_check = requestDto.getPassword_check();

        //pw 정규식 검사
        if(!Validator.pwValid(username, password)){
            throw new IllegalArgumentException("pwValid");
        }

        //비밀번호 재입력 일치 검사
        if (!password.equals(password_check)) {
            throw new IllegalArgumentException("notequal");
        }
        //모든조건 충족시 비밀번호 암호화
        password = passwordEncoder.encode(requestDto.getPassword());

        //Email
        String email = requestDto.getEmail();
        if (!Validator.emailValid(email)){
            throw new IllegalArgumentException("emailValid");
        }
        //중복검사
        Optional<User> found2 = userRepository.findByEmail(email);
        if (found2.isPresent()) {
            throw new IllegalArgumentException("Emailduplicate");
        }


        User user = new User(username, password, email);
        userRepository.save(user);
    }

    public void kakaoLogin(String authorizedCode) {
        // 카카오 OAuth2 를 통해 카카오 사용자 정보 조회
        KakaoUserInfo userInfo = kakaoOAuth2.getUserInfo(authorizedCode);
        Long kakaoId = userInfo.getId();
        String nickname = userInfo.getNickname();
        String email = userInfo.getEmail();

        // DB 에 중복된 Kakao Id 가 있는지 확인
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        if (kakaoUser == null) {

            // 카카오 이메일과 동일한 이메일을 가진 회원이 있는지 확인
            User sameEmailUser = userRepository.findByEmail(email).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 카카오 이메일과 동일한 이메일 회원이 있는 경우
                // 카카오 Id 를 회원정보에 저장
                kakaoUser.setKakaoId(kakaoId);
                userRepository.save(kakaoUser);

            } else {
                // 카카오 정보로 회원가입
                // username = 카카오 nickname
                String username = nickname;
                // password = 카카오 Id + ADMIN TOKEN
                String password = kakaoId + "dsfasfjskadhfjkadslhfjklasasdfhfjklasddhlfasdjhjklhadsfkjfsdhajkfhasjkasfdhkljjkasd";
                // 패스워드 인코딩
                String encodedPassword = passwordEncoder.encode(password);

                kakaoUser = new User(username, encodedPassword, email, kakaoId);
                userRepository.save(kakaoUser);
            }
        }

        // 강제 로그인 처리
        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}