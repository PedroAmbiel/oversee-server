package com.oversee.client;

public class PedidoDeValidacaoLogin extends Comunicado{

    private String login;
    private String senha;


    public PedidoDeValidacaoLogin(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public static boolean validarLogin(PedidoDeValidacaoLogin pedidoDeValidacaoLogin) {
        if(!pedidoDeValidacaoLogin.getSenha().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).+$")){
            return false;
        }else
            return PedidoDeValidacaoCpfCnpj.validarCpfCnpj(new PedidoDeValidacaoCpfCnpj(pedidoDeValidacaoLogin.getLogin()));
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
