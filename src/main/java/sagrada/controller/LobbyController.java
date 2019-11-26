package sagrada.controller;

import sagrada.model.Account;

public class LobbyController {
    private final Account user;

    public LobbyController(Account account) {
        this.user = account;
    }
}
