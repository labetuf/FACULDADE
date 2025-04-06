package org.example;

import org.example.entities.Product;
import org.example.repository.ProductRepository;

import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ProductRepository serviceRepository = null;
        Connection conn = null;

        String url = "jdbc:sqlite:database.sqlite";

        try {
            conn = DriverManager.getConnection(url);

            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS services (" +
                                    "uuid TEXT PRIMARY KEY, " +
                                    "description TEXT NOT NULL, " +
                                    "hourly_rate REAL NOT NULL" +
                                    ");"
                    );

                    stmt.executeUpdate(
                            "CREATE TABLE IF NOT EXISTS users (" +
                                    "uuid TEXT PRIMARY KEY, " +
                                    "name TEXT NOT NULL, " +
                                    "email TEXT NOT NULL, " +
                                    "password TEXT NOT NULL, " +
                                    "role TEXT NOT NULL" +
                                    ");"
                    );

                    System.out.println("Tabelas criadas com sucesso.");
                } catch (SQLException e) {
                    System.out.println("Erro ao criar tabelas:");
                    e.printStackTrace();
                    System.exit(1);
                }

                serviceRepository = new ProductRepository(conn);
            } else {
                System.out.println("Falha na conexão com o banco de dados.");
                System.exit(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar:");
            e.printStackTrace();
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n--- MENU ---");
            System.out.println("1 - Cadastrar Serviço");
            System.out.println("2 - Listar Serviços");
            System.out.println("3 - Sair");
            System.out.println("4 - Limpar todos os serviços");
            System.out.print("Escolha uma opção: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Descrição do serviço: ");
                    String descricao = scanner.nextLine();
                    System.out.print("Valor por hora: ");
                    double valor = scanner.nextDouble();
                    scanner.nextLine();
                    Product novoServico = new Product(descricao, valor);
                    serviceRepository.save(novoServico);
                    System.out.println("Serviço cadastrado com sucesso.");
                    break;

                case 2:
                    List<Product> servicos = serviceRepository.findAll();
                    if (servicos.isEmpty()) {
                        System.out.println("Nenhum serviço encontrado.");
                    } else {
                        for (Product servico : servicos) {
                            System.out.println(servico);
                        }
                    }
                    break;

                case 3:
                    System.out.println("Saindo...");
                    break;

                case 4:
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("DELETE FROM services;");
                        System.out.println("Todos os serviços foram removidos com sucesso.");
                    } catch (SQLException e) {
                        System.out.println("Erro ao limpar a tabela:");
                        e.printStackTrace();
                    }
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (option != 3);

        scanner.close();
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexão:");
            e.printStackTrace();
        }
    }
}
