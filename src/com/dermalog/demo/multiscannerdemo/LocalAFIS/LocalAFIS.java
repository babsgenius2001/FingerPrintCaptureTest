/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.LocalAFIS;

import java.util.HashMap;
import java.util.List;

import com.dermalog.afis.fingercode3.FC3Exception;
import com.dermalog.afis.fingercode3.Matcher;
import com.dermalog.demo.multiscannerdemo.FPScanner.Fingerprint;

/**
 *
 * @author BA07190
 */
public class LocalAFIS {

    private HashMap<Long, LocalUser> _userList;
    private long _nextId = 0;

    Matcher matcher;

    public LocalAFIS() throws FC3Exception {
        _userList = LocalDB.convertFoldersToUserList();
        _nextId = getMaxId();
        matcher = new Matcher();
    }

    public boolean IsEmpty() {
        return _userList.size() == 0;
    }

    private long getMaxId() {
        if (_userList.keySet().size() == 0) {
            return 0;
        }
        long maxId = 0;
        for (Long id : _userList.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
    }

    private long getNextId() {
        return ++_nextId;
    }

    public HashMap<Long, LocalUser> GetUserList() {
        return _userList;
    }

    public LocalUser RegisterUser(String name, List<Fingerprint> fingerprints) {
        LocalUser localUser = new LocalUser();
        localUser.ID = getNextId();
        localUser.Name = name;
        localUser.Fingerprints = fingerprints;

        _userList.put(localUser.ID, localUser);

        LocalDB.createUserFolder(localUser);

        return localUser;
    }

    public AFISVerificationResult VerifyUser(long userId,
            List<Fingerprint> fingerprints, double threshold) throws Exception {
        AFISVerificationResult result = new AFISVerificationResult();

        if (!_userList.containsKey(userId)) {
            throw new Exception("USER NOT REGISTERED");
        }

        LocalUser user = _userList.get(userId);
        List<Fingerprint> userFingerprints = user.Fingerprints;

        double dMaxScore = 0.0;

        for (int i = 0; i < userFingerprints.size(); i++) {
            for (int j = 0; j < fingerprints.size(); j++) {
                double dScore = matcher.Match(userFingerprints.get(i).Template,
                        fingerprints.get(j).Template);
                if (dScore > threshold && dScore > dMaxScore) {
                    dMaxScore = dScore;
                    result.Score = dMaxScore;
                    result.Hit = true;
                }
            }
        }

        return result;
    }

    public class AFISVerificationResult {

        public double Score;

        public boolean Hit = false;
    }
}
