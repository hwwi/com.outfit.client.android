package com.outfit.client.android.db

import com.outfit.client.android.data.IEntity

// upsert : https://stackoverflow.com/questions/45677230/android-room-persistence-library-upsert
abstract class AbstractDao<TEntity : IEntity<TEntityId>, TEntityId>