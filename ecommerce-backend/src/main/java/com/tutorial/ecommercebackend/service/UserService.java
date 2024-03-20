package com.tutorial.ecommercebackend.service;

import com.tutorial.ecommercebackend.api.model.LoginBody;
import com.tutorial.ecommercebackend.api.model.PasswordResetBody;
import com.tutorial.ecommercebackend.api.model.RegistrationBody;
import com.tutorial.ecommercebackend.exception.EmailFailureException;
import com.tutorial.ecommercebackend.exception.EmailNotFoundException;
import com.tutorial.ecommercebackend.exception.UserAlreadyExistsException;
import com.tutorial.ecommercebackend.exception.UserNotVerifiedException;
import com.tutorial.ecommercebackend.model.LocalUser;
import com.tutorial.ecommercebackend.model.VerificationToken;
import com.tutorial.ecommercebackend.model.dao.LocalUserDAO;
import com.tutorial.ecommercebackend.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private LocalUserDAO localUserDAO;
  private EncryptionService encryptionService;
  private JWTService jwtService;
  private EmailService emailService;
  private VerificationTokenDAO verificationTokenDAO;

  public UserService(LocalUserDAO localUserDAO, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenDAO verificationTokenDAO) {
    this.localUserDAO = localUserDAO;
    this.encryptionService = encryptionService;
    this.jwtService = jwtService;
    this.emailService = emailService;
    this.verificationTokenDAO = verificationTokenDAO;
  }

  public LocalUser registerUser(RegistrationBody registrationBody)
          throws UserAlreadyExistsException, EmailFailureException {

    if(localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()
        || localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent())  {
      throw new UserAlreadyExistsException();
    }

    LocalUser user = new LocalUser();
    String encryptedPassword = encryptionService
                                  .encryptPassword(registrationBody.getPassword());

    user.setUsername(registrationBody.getUsername());
    user.setFirstName(registrationBody.getFirstName());
    user.setEmail(registrationBody.getEmail());
    user.setLastName(registrationBody.getLastName());
    user.setPassword(encryptedPassword);

    VerificationToken verificationToken = createVerificationToken(user);
    emailService.sendVerificationEmail(verificationToken);
    verificationTokenDAO.save(verificationToken);

    return localUserDAO.save(user);
  }

  private VerificationToken createVerificationToken(LocalUser user) {
    VerificationToken verificationToken = new VerificationToken();

    verificationToken.setToken(jwtService.generateVerificationJWT(user));
    verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
    verificationToken.setUser(user);
    user.getVerificationTokens().add(verificationToken);

    return verificationToken;
  }

  public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
    Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());

    if(opUser.isPresent()) {
      LocalUser user = opUser.get();
      if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
        if(user.isEmailVerified()) {
          return jwtService.generateJWT(user);
        } else {
          List<VerificationToken> verificationTokens = user.getVerificationTokens();
          boolean resend = verificationTokens.isEmpty() ||
                  verificationTokens.get(0).getCreatedTimestamp()
                          .before((new Timestamp((System.currentTimeMillis()) - (60 * 6-0 * 1000))));

          if(resend) {
            VerificationToken verificationToken = createVerificationToken(user);
            emailService.sendVerificationEmail(verificationToken);
            verificationTokenDAO.save(verificationToken);
          }
          throw new UserNotVerifiedException(resend);
        }
      }
    }
    return null;
  }

  @Transactional
  public boolean verifyUser(String token) {
    Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);

    if(opToken.isPresent()) {
      VerificationToken verificationToken = opToken.get();
      LocalUser user = verificationToken.getUser();
      if(!user.isEmailVerified()) {
        user.setEmailVerified(true);
        localUserDAO.save(user);
        verificationTokenDAO.deleteByUser(user);
        return true;
      }
    }

    return false;
  }

  public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
    Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);

    if(opUser.isPresent()) {
      LocalUser user = opUser.get();
      String token = jwtService.generatePasswordResetJWT(user);
      emailService.sendPasswordResetEmail(user, token);
    } else {
      throw new EmailNotFoundException();
    }
  }

  public void resetPassword(PasswordResetBody body) {
    String email = jwtService.getResetPasswordEmailKey(body.getToken());
    Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);

    if(opUser.isPresent()) {
      LocalUser user = opUser.get();
      String newPassword = encryptionService.encryptPassword(body.getNewPassword());
      user.setPassword(newPassword);
      localUserDAO.save(user);
    }
  }
}
