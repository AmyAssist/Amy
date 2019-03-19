/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package io.github.amyassist.amy.plugin.webpush;

import static nl.martijndwars.webpush.Utils.*;
import static org.bouncycastle.jce.provider.BouncyCastleProvider.*;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;

import io.github.amyassist.amy.core.di.annotation.PostConstruct;
import io.github.amyassist.amy.core.di.annotation.Reference;
import io.github.amyassist.amy.core.di.annotation.Service;
import io.github.amyassist.amy.core.plugin.api.IStorage;
import io.github.amyassist.amy.plugin.webpush.model.Subscription;
import io.github.amyassist.amy.plugin.webpush.persistence.SubscriptionEntity;
import io.github.amyassist.amy.plugin.webpush.persistence.SubscriptionStorage;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;

/**
 * Implementation of the WebPushService using the BouncyCastleProvider for encryption.
 * 
 * @author Leon Kiefer
 */
@Service
public class WebPushServiceImpl implements WebPushService {

	private static final String PUBLIC_KEY = "PublicKey";
	private static final String PRIVATE_KEY = "PrivateKey";

	@Reference
	private Logger logger;

	/**
	 * A reference to the storage.
	 */
	@Reference
	private IStorage storage;
	@Reference
	private SubscriptionStorage subscriptionStorage;

	@PostConstruct
	private void setup() {
		Security.addProvider(new BouncyCastleProvider());

		if (!(this.storage.has(PUBLIC_KEY) && this.storage.has(PRIVATE_KEY))) {
			this.logger.info("Create new key pair");
			try {
				KeyPair keyPair = this.generateKeyPair();

				byte[] publicKey = Utils.encode((ECPublicKey) keyPair.getPublic());
				byte[] privateKey = Utils.encode((ECPrivateKey) keyPair.getPrivate());

				this.storage.put(PUBLIC_KEY, Base64.getUrlEncoder().encodeToString(publicKey));
				this.storage.put(PRIVATE_KEY, Base64.getUrlEncoder().encodeToString(privateKey));
			} catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException e) {
				this.logger.error("Could not generate key pair", e);
			}
		}

	}

	/**
	 * Generate an EC keypair on the prime256v1 curve.
	 * 
	 * @return the generated key pair
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	private KeyPair generateKeyPair()
			throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
		ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(CURVE);

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER_NAME);
		keyPairGenerator.initialize(parameterSpec);

		return keyPairGenerator.generateKeyPair();
	}

	private Future<HttpResponse> sendPushMessage(SubscriptionEntity sub, byte[] payload) {
		try {
			Notification notification = new Notification(sub.getEndpoint(), this.getUserPublicKey(sub),
					this.getAuthAsBytes(sub), payload);
			String publicKey = this.storage.get(PUBLIC_KEY);
			String privateKey = this.storage.get(PRIVATE_KEY);
			PushService pushService = new PushService(publicKey, privateKey, "testSubject");
			return pushService.sendAsync(notification);
		} catch (GeneralSecurityException | IOException | JoseException e) {
			throw new IllegalStateException(e);
		}
	}

	private PublicKey getUserPublicKey(SubscriptionEntity sub)
			throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		KeyFactory kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
		ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");

		byte[] decode = Base64.getUrlDecoder().decode(sub.getKey());

		ECPoint point = ecSpec.getCurve().decodePoint(decode);
		ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);

		return kf.generatePublic(pubSpec);
	}

	private byte[] getAuthAsBytes(SubscriptionEntity sub) {
		return Base64.getUrlDecoder().decode(sub.getAuth());
	}

	@Override
	public int subscribe(Subscription subscription) {
		SubscriptionEntity subscriptionEntity = new SubscriptionEntity(subscription.getEndpoint(),
				subscription.getAuth(), subscription.getKey());
		this.subscriptionStorage.save(subscriptionEntity);
		return subscriptionEntity.getPersistentId();
	}

	@Override
	public void unsubscribe(int id) {
		if (this.subscriptionStorage.getById(id) == null) {
			throw new NoSuchElementException("No Subscription with id: " + id);
		}
		this.subscriptionStorage.deleteById(id);
	}

	@Override
	public String getPublicVAPIDKey() {
		return this.storage.get(PUBLIC_KEY);
	}

	@Override
	public void sendPushNotification(int id, byte[] payload) {
		SubscriptionEntity subscriptionEntity = this.subscriptionStorage.getById(id);
		if (subscriptionEntity == null) {
			throw new NoSuchElementException("No Subscription with id: " + id);
		}
		this.sendPushMessage(subscriptionEntity, payload);
	}
}
