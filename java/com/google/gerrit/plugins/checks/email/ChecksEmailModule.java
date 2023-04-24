// Copyright (C) 2019 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.plugins.checks.email;

import static com.google.inject.Scopes.SINGLETON;

import com.google.gerrit.entities.Change;
import com.google.gerrit.entities.Project;
import com.google.gerrit.extensions.config.FactoryModule;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.mail.send.ChangeEmailNew;
import com.google.gerrit.server.mail.send.ChangeEmailNewFactory;
import com.google.gerrit.server.mail.send.EmailArguments;
import com.google.gerrit.server.mail.send.MailSoyTemplateProvider;
import com.google.gerrit.server.mail.send.OutgoingEmailNew;
import com.google.gerrit.server.mail.send.OutgoingEmailNewFactory;
import com.google.inject.Inject;

public class ChecksEmailModule extends FactoryModule {
  @Override
  protected void configure() {
    DynamicSet.bind(binder(), MailSoyTemplateProvider.class)
        .to(ChecksMailSoyTemplateProvider.class)
        .in(SINGLETON);
  }

  public static class ChecksEmailFactories {
    private final EmailArguments args;
    private final ChangeEmailNewFactory changeEmailFactory;
    private final OutgoingEmailNewFactory outgoingEmailFactory;

    @Inject
    ChecksEmailFactories(
        EmailArguments args,
        ChangeEmailNewFactory changeEmailFactory,
        OutgoingEmailNewFactory outgoingEmailFactory) {
      this.args = args;
      this.changeEmailFactory = changeEmailFactory;
      this.outgoingEmailFactory = outgoingEmailFactory;
    }

    public CombinedCheckStateUpdatedChangeEmailDecorator createChecksEmailDecorator() {
      return new CombinedCheckStateUpdatedChangeEmailDecorator();
    }

    public ChangeEmailNew createChangeEmail(
        Project.NameKey project,
        Change.Id changeId,
        CombinedCheckStateUpdatedChangeEmailDecorator checksEmailDecorator) {
      return changeEmailFactory.create(args.newChangeData(project, changeId), checksEmailDecorator);
    }

    public OutgoingEmailNew createEmail(ChangeEmailNew changeEmail) {
      return outgoingEmailFactory.create("combinedCheckStateUpdate", changeEmail);
    }
  }
}
