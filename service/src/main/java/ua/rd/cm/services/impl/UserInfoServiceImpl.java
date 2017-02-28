package ua.rd.cm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.rd.cm.domain.UserInfo;
import ua.rd.cm.repository.UserInfoRepository;
import ua.rd.cm.repository.specification.userinfo.UserInfoById;
import ua.rd.cm.services.UserInfoService;
import ua.rd.cm.services.exception.EntityNotFoundException;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private UserInfoRepository userInfoRepository;

    @Autowired
    public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public UserInfo find(Long id) {
        List<UserInfo> usersInfo = userInfoRepository.findBySpecification(new UserInfoById(id));
        if (usersInfo.isEmpty()) {
            throw new EntityNotFoundException("user_info_not_found");
        }
        return usersInfo.get(0);
    }

    @Override
    @Transactional
    public void save(UserInfo userInfo) {
        userInfoRepository.saveUserInfo(userInfo);
    }

    @Override
    @Transactional
    public void update(UserInfo userInfo) {
        userInfoRepository.updateUserInfo(userInfo);
    }
}
