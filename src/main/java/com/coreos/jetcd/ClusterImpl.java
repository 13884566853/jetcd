package com.coreos.jetcd;

import com.coreos.jetcd.api.ClusterGrpc;
import com.coreos.jetcd.api.MemberAddRequest;
import com.coreos.jetcd.api.MemberAddResponse;
import com.coreos.jetcd.api.MemberListRequest;
import com.coreos.jetcd.api.MemberListResponse;
import com.coreos.jetcd.api.MemberRemoveRequest;
import com.coreos.jetcd.api.MemberRemoveResponse;
import com.coreos.jetcd.api.MemberUpdateRequest;
import com.coreos.jetcd.api.MemberUpdateResponse;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.javacrumbs.futureconverter.java8guava.FutureConverter;

/**
 * Implementation of cluster client.
 */
public class ClusterImpl implements Cluster {

  private final ClusterGrpc.ClusterFutureStub stub;

  public ClusterImpl(ManagedChannel channel, Optional<String> token) {
    this.stub = ClientUtil.configureStub(ClusterGrpc.newFutureStub(channel), token);
  }

  /**
   * lists the current cluster membership.
   */
  @Override
  public CompletableFuture<MemberListResponse> listMember() {
    return FutureConverter
        .toCompletableFuture(stub.memberList(MemberListRequest.getDefaultInstance()));
  }

  /**
   * add a new member into the cluster.
   *
   * @param endpoints the address of the new member
   */
  @Override
  public CompletableFuture<MemberAddResponse> addMember(List<String> endpoints) {
    MemberAddRequest memberAddRequest = MemberAddRequest.newBuilder().addAllPeerURLs(endpoints)
        .build();
    return FutureConverter.toCompletableFuture(stub.memberAdd(memberAddRequest));
  }

  /**
   * removes an existing member from the cluster.
   *
   * @param memberID the id of the member
   */
  @Override
  public CompletableFuture<MemberRemoveResponse> removeMember(long memberID) {
    MemberRemoveRequest memberRemoveRequest = MemberRemoveRequest.newBuilder().setID(memberID)
        .build();
    return FutureConverter.toCompletableFuture(stub.memberRemove(memberRemoveRequest));
  }

  /**
   * update peer addresses of the member.
   *
   * @param memberID the id of member to update
   * @param endpoints the new endpoints for the member
   */
  @Override
  public CompletableFuture<MemberUpdateResponse> updateMember(long memberID,
      List<String> endpoints) {
    MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.newBuilder()
        .addAllPeerURLs(endpoints)
        .setID(memberID)
        .build();
    return FutureConverter.toCompletableFuture(stub.memberUpdate(memberUpdateRequest));
  }
}
