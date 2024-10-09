package com.oversee.client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.random.RandomGenerator;

public class SupervisoraDeConexao extends Thread
{
    private double              valor=0;
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao
    (Socket conexao, ArrayList<Parceiro> usuarios)
    throws Exception
    {
        if (conexao==null)
            throw new Exception ("Conexao ausente");

        if (usuarios==null)
            throw new Exception ("Usuarios ausentes");

        this.conexao  = conexao;
        this.usuarios = usuarios;
    }

    public void run ()
    {
        Random random = new Random();

        ObjectOutputStream transmissor;
        try
        {
            transmissor =
            new ObjectOutputStream(
            this.conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            return;
        }
        
        ObjectInputStream receptor=null;
        try
        {
            receptor=
            new ObjectInputStream(
            this.conexao.getInputStream());
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread
            
            return;
        }

        try
        {
            this.usuario =
            new Parceiro (this.conexao,
                          receptor,
                          transmissor);
        }
        catch (Exception erro)
        {} // sei que passei os parametros corretos

        try
        {
            synchronized (this.usuarios)
            {
                this.usuarios.add (this.usuario);
            }

            int numThread = random.nextInt(100);
            for(;;)
            {
                Comunicado comunicado = this.usuario.envie ();
                System.out.println("Iniciado a thread " + numThread);

                if (comunicado==null) {
                    return;
                }
                else if(comunicado instanceof PedidoDeValidacaoCpfCnpj){
                    //System.out.println("Entrou no pedido de validar CPF/CNPJ");

                    PedidoDeValidacaoCpfCnpj pedido = (PedidoDeValidacaoCpfCnpj)comunicado;

                    //System.out.println("Thread de num " + numThread + " entrando em sleep 5000");
                    //sleep(5000);

                    this.usuario.receba(PedidoDeValidacaoCpfCnpj.validarCpfCnpj(pedido));

                    System.out.println("Pedido de CPF/CNPJ da thread " + numThread + " enviado \n\n");
                }
                else if(comunicado instanceof PedidoDeValidacaoLogin){

                    PedidoDeValidacaoLogin login = (PedidoDeValidacaoLogin)comunicado;

                    this.usuario.receba(PedidoDeValidacaoLogin.validarLogin(login));

                    System.out.println("Pedido de Login da thread " + numThread + " enviado \n\n");

                }else if(comunicado instanceof PedidoDeValidacaoNovoPrestador){

                    PedidoDeValidacaoNovoPrestador novoPrestador = (PedidoDeValidacaoNovoPrestador)comunicado;

                    this.usuario.receba(PedidoDeValidacaoNovoPrestador.validarNovoPrestador(novoPrestador));

                    System.out.println("Pedido de Novo Cadastro Prestador da thread " + numThread + " enviado \n\n");

                } else if (comunicado instanceof PedidoDeValidacaoEstado) {
                    PedidoDeValidacaoEstado estado = (PedidoDeValidacaoEstado)comunicado;

                    this.usuario.receba(PedidoDeValidacaoEstado.verificarEstado(estado.getEstado()));

                    System.out.println("Pedido de Validar Estado da thread " + numThread + " enviado \n\n");

                } else if (comunicado instanceof PedidoDeValidacaoEmail) {
                    PedidoDeValidacaoEmail email = (PedidoDeValidacaoEmail) comunicado;

                    this.usuario.receba(PedidoDeValidacaoEmail.validarEmail(email));

                    System.out.println("Pedido de Validar Estado da thread " + numThread + " enviado \n\n");
                } else if (comunicado instanceof PedidoParaSair) {
                    synchronized (this.usuarios) {
                        this.usuarios.remove(this.usuario);
                    }
                    this.usuario.adeus();
                }
            }
        }
        catch (Exception erro)
        {
            try
            {
                transmissor.close ();
                receptor   .close ();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }
    }
}
