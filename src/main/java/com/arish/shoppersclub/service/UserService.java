package com.arish.shoppersclub.service;

import com.arish.shoppersclub.dto.request.UpdateProfileRequest;
import com.arish.shoppersclub.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile();
    UserProfileResponse updateProfile(UpdateProfileRequest request);
}
