package io.jhchoe.familytree.core.invite.adapter.in.response;

/**
 * SaveFamilyInviteResponse는 초대 생성 응답을 위한 DTO입니다.
 */
public record SaveFamilyInviteResponse(
    String inviteCode,
    String inviteUrl
) {
}