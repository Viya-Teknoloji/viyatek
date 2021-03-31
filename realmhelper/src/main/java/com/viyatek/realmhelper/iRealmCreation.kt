package com.viyatek.realmhelper

import io.realm.Realm

interface iRealmCreation {
    fun beforeRealmCreated(populatedRealm : Realm)
}