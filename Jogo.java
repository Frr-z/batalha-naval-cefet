// Diego Martins, Daniel Ferraz e Antônio Dornelas

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Jogo {

    final int TamanhoDaGrade = 14;
    int[][] GradeMaquina = new int[TamanhoDaGrade][TamanhoDaGrade];
    int[][] GradeUsuario = new int[TamanhoDaGrade][TamanhoDaGrade];

    class CPU {
        public int SubmarinosColocados = 0;
        public int ContratorpedeirosColocados = 0;
        public int CruzadoresColocados = 0;
        public int PortaAvioesColocados = 0;

        public static int Pontos = 0;
    }

    class User {
        public int SubmarinosColocados = 0;
        public int ContratorpedeirosColocados = 0;
        public int CruzadoresColocados = 0;
        public int PortaAvioesColocados = 0;

        public static int Pontos = 0;
    }

    public class IA {
        private List<int[]> PossiveisAlvos = new ArrayList<>();
        private boolean AlvoAtual = false;
        private int[] UltimoAcerto = new int[2];
    
        public void Atirar(int[][] GradeUsuario) {
            int Linha, Coluna;
    
            if (AlvoAtual && !PossiveisAlvos.isEmpty()) {
                int[] alvoAchado = PossiveisAlvos.remove(0);
                Linha = alvoAchado[0];
                Coluna = alvoAchado[1];
            } else {
                Linha = (int) (Math.random() * TamanhoDaGrade);
                Coluna = (int) (Math.random() * TamanhoDaGrade);
            }
    
            if (GradeUsuario[Linha][Coluna] == 1) {
                GradeUsuario[Linha][Coluna] = 2; // acerto
                CPU.Pontos++;
                AlvoAtual = true;
                UltimoAcerto[0] = Linha;
                UltimoAcerto[1] = Coluna;
                AdicionarPossiveisAlvos(Linha, Coluna, GradeUsuario);
            } else if (GradeUsuario[Linha][Coluna] == 0) {
                GradeUsuario[Linha][Coluna] = 3; // água
                AlvoAtual = false;
            }
    
            Vez = (Vez == 1) ? 0 : 1;
        }
    
        private void AdicionarPossiveisAlvos(int Linha, int Coluna, int[][] GradeUsuario) {
            if (Linha - 1 >= 0 && GradeUsuario[Linha - 1][Coluna] != 2) 
                PossiveisAlvos.add(new int[]{Linha - 1, Coluna});
            if (Linha + 1 < TamanhoDaGrade && GradeUsuario[Linha + 1][Coluna] != 2) 
                PossiveisAlvos.add(new int[]{Linha + 1, Coluna});
            if (Coluna - 1 >= 0 && GradeUsuario[Linha][Coluna - 1] != 2) 
                PossiveisAlvos.add(new int[]{Linha, Coluna - 1});
            if (Coluna + 1 < TamanhoDaGrade && GradeUsuario[Linha][Coluna + 1] != 2) 
                PossiveisAlvos.add(new int[]{Linha, Coluna + 1});
        }
    }

    User Jogador = new User();
    CPU InimigoNormal = new CPU();
    IA InimigoInteligente = new IA();

    private int Vez = 1;
    private int Dificuldade = 1;

    private Scanner Input = new Scanner(System.in);

    final String[] Alfabeto = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};

    int LetraParaNumero(String Letra) {
        return Arrays.asList(Alfabeto).indexOf(Letra.toUpperCase());
    }

    private int Arredondar(int Angulo) {
        int[] AngulosPossiveis = {0, 90, 180, 270};
        int AnguloMaisProximo = AngulosPossiveis[0];
        int DiferencaMinima = Math.abs(Angulo - AngulosPossiveis[0]);

        for (int i = 1; i < AngulosPossiveis.length; i++) {
            int Diferenca = Math.abs(Angulo - AngulosPossiveis[i]);
            if (Diferenca < DiferencaMinima) {
                AnguloMaisProximo = AngulosPossiveis[i];
                DiferencaMinima = Diferenca;
            }
        }

        return AnguloMaisProximo;
    }

    public static void main(String[] args) {
        Jogo Jogo = new Jogo();
        Jogo.Inicializar();
        Jogo.Comecar();
    }

    public void Inicializar() {
        for (int i = 0; i < TamanhoDaGrade; i++) {
            for (int j = 0; j < TamanhoDaGrade; j++) {
                GradeMaquina[i][j] = 0;
                GradeUsuario[i][j] = 0;
            }
        }
        System.out.println("Bem-vindo ao jogo de Batalha Naval!");
        System.out.print("Escolha a Dificuldade:\n(1) - Fácil\n(2) - Difícil\nEscolha: ");
        Dificuldade = Input.nextInt();
    }

    public void Comecar() {
        System.out.println("Gerando automaticamente seus navios:");
        PosicionarBarcosAutomaticamente(GradeUsuario, Jogador);

        System.out.println("Sua grade gerada automaticamente:");
        Exibir(GradeUsuario);

        boolean Reposicionar = PerguntarSeReposicionar();
        if (Reposicionar) {
            System.out.println("Reposicione seus navios:");
            ReposicionarBarcos(GradeUsuario, Jogador);
        }

        System.out.println("IA está colocando os navios...");
        PosicionarBarcosAutomaticamente(GradeMaquina, InimigoNormal);
        ExibirGrades();

        while (true) {
            if (Vez == 1) {
                System.out.println("Sua vez de atirar!");
                AtirarJogador(GradeMaquina);
            } else {
                System.out.println("Vez da IA de atirar!");
                if(Dificuldade == 2){
                    InimigoInteligente.Atirar(GradeUsuario);
                }else{
                    AtirarInimigo();
                }
            }
            ExibirGrades();
            System.out.println("Pontuação: Jogador = " + User.Pontos + ", IA = " + CPU.Pontos);
            if (User.Pontos >= 20 || CPU.Pontos >= 20) {
                break;
            }
        }

        if (User.Pontos > CPU.Pontos) {
            System.out.println("Você venceu!");
        } else {
            System.out.println("A IA venceu!");
        }
    }


    public void ExibirGrades() {
        System.out.println("Jogador: " + User.Pontos + " | IA: " + CPU.Pontos);

        System.out.println("Grade do Jogador:");
        Exibir(GradeUsuario);

        System.out.println("Grade da IA:");
        Exibir(GradeMaquina);
    }

    public void Exibir(int[][] Grade) {
        System.out.print("  ");
        for (String Letra : Alfabeto) {
            System.out.print("  " + Letra + " ");
        }
        System.out.println();
        for (int i = 0; i < TamanhoDaGrade; i++) {
            System.out.print((i + 1) + (i < 9 ? " " : ""));
            for (int j = 0; j < TamanhoDaGrade; j++) {
                if (Grade[i][j] == 1 /*&& Grade == GradeUsuario*/) {
                    System.out.print("| " + "O" + " ");
                } else if (Grade[i][j] == 2) {
                    System.out.print("| " + "*" + " ");
                } else if (Grade[i][j] == 3) {
                    System.out.print("| " + "X" + " ");
                } else {
                    System.out.print("| " + " " + " ");
                }
            }
            System.out.println("|");
        }
    }

    public boolean PosicaoValida(int Linha, int Coluna, int Tamanho, int Rotacao, int[][] Grade) {
        if (Rotacao == 0) { // Horizontal para a direita
            if (Coluna + Tamanho > TamanhoDaGrade) return false;
            for (int i = Coluna; i < Coluna + Tamanho; i++) {
                if (Grade[Linha][i] != 0 || !CasaAdjacenteVazia(Linha, i, Grade)) return false;
            }
        } else if (Rotacao == 90) { // Vertical para baixo
            if (Linha + Tamanho > TamanhoDaGrade) return false;
            for (int i = Linha; i < Linha + Tamanho; i++) {
                if (Grade[i][Coluna] != 0 || !CasaAdjacenteVazia(i, Coluna, Grade)) return false;
            }
        } else if (Rotacao == 180) { // Horizontal para a esquerda
            if (Coluna - Tamanho < -1) return false;
            for (int i = Coluna; i > Coluna - Tamanho; i--) {
                if (Grade[Linha][i] != 0 || !CasaAdjacenteVazia(Linha, i, Grade)) return false;
            }
        } else if (Rotacao == 270) { // Vertical para cima
            if (Linha - Tamanho < -1) return false;
            for (int i = Linha; i > Linha - Tamanho; i--) {
                if (Grade[i][Coluna] != 0 || !CasaAdjacenteVazia(i, Coluna, Grade)) return false;
            }
        }
        return true;
    }

    private boolean CasaAdjacenteVazia(int Linha, int Coluna, int[][] Grade) {
        for (int i = Linha - 1; i <= Linha + 1; i++) {
            for (int j = Coluna - 1; j <= Coluna + 1; j++) {
                if (i >= 0 && i < TamanhoDaGrade && j >= 0 && j < TamanhoDaGrade && Grade[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void AtirarJogador(int[][] Grid) {
        System.out.println("Informe a Linha [1-14]: ");
        int Linha = Input.nextInt() - 1;
        System.out.println("Informe a Coluna [A-N]: ");
        String colStr = Input.next();
        int Coluna = LetraParaNumero(colStr);

        if (Grid[Linha][Coluna] == 1) {
            Grid[Linha][Coluna] = 2; // acerto
            User.Pontos++;
           // System.out.println(BarcoAfundado(Grid, Linha, Coluna));
        } else if (Grid[Linha][Coluna] == 0) {
            Grid[Linha][Coluna] = 3; // água
        }
        Vez = (Vez == 1) ? 0 : 1;
    }

    public void AtirarInimigo() {
        int Linha = (int) (Math.random() * TamanhoDaGrade);
        int Coluna = (int) (Math.random() * TamanhoDaGrade);

        if (GradeUsuario[Linha][Coluna] == 1) {
            GradeUsuario[Linha][Coluna] = 2; // acerto
            CPU.Pontos++;
        } else if (GradeUsuario[Linha][Coluna] == 0) {
            GradeUsuario[Linha][Coluna] = 3; // água
        }
        Vez = (Vez == 1) ? 0 : 1;
    }

    public void PosicionarBarcosAutomaticamente(int[][] Grade, User Jogador) {
        ColocarNavios(Grade, Jogador, 5, 1); // 1 Porta-Aviões
        ColocarNavios(Grade, Jogador, 4, 2); // 2 Navios Tanque
        ColocarNavios(Grade, Jogador, 3, 3); // 3 Contratorpedeiros
        ColocarNavios(Grade, Jogador, 2, 4); // 4 Submarinos
    }

    public void PosicionarBarcosAutomaticamente(int[][] Grade, CPU IA) {
        ColocarNavios(Grade, IA, 5, 1); // 1 Porta-Aviões
        ColocarNavios(Grade, IA, 4, 2); // 2 Navios Tanque
        ColocarNavios(Grade, IA, 3, 3); // 3 Contratorpedeiros
        ColocarNavios(Grade, IA, 2, 4); // 4 Submarinos
    }

    public void ColocarNavios(int[][] Grade, User Jogador, int Tamanho, int Quantidade) {
        for (int i = 0; i < Quantidade; i++) {
            boolean colocado = false;
            while (!colocado) {
                String LetraAleatoria = Alfabeto[(int) (Math.random() * Alfabeto.length)];
                int NumeroAleatorio = (int) (Math.random() * TamanhoDaGrade);
                int AnguloAleatorio = Arredondar((int) (Math.random() * 360));

                if (Posicionar(NumeroAleatorio, LetraAleatoria, AnguloAleatorio, Tamanho, Grade)) {
                    colocado = true;
                }
            }
        }
    }

    public void ColocarNavios(int[][] Grade, CPU IA, int Tamanho, int Quantidade) {
        for (int i = 0; i < Quantidade; i++) {
            boolean Colocado = false;
            while (!Colocado) {
                String LetraAleatoria = Alfabeto[(int) (Math.random() * Alfabeto.length)];
                int NumeroAleatorio = (int) (Math.random() * TamanhoDaGrade);
                int AnguloAleatorio = Arredondar((int) (Math.random() * 360));

                if (Posicionar(NumeroAleatorio, LetraAleatoria, AnguloAleatorio, Tamanho, Grade)) {
                    Colocado = true;
                }
            }
        }
    }

    public boolean PerguntarSeReposicionar() {
        System.out.println("Deseja reposicionar algum navio? (s/n): ");
        String Resposta = Input.next();
        return Resposta.equalsIgnoreCase("s");
    }

    public void ReposicionarBarcos(int[][] Grade, User Jogador) {
    System.out.println("Informe o tipo de navio que deseja reposicionar: ");
    System.out.println("1. Porta-Aviões (5 quadrados)");
    System.out.println("2. Navios Tanque (4 quadrados)");
    System.out.println("3. Contratorpedeiros (3 quadrados)");
    System.out.println("4. Submarinos (2 quadrados)");
    int Tipo = Input.nextInt();

    int Tamanho = 0;

    switch (Tipo) {
        case 1:
            Tamanho = 5;
            break;
        case 2:
            Tamanho = 4;
            break;
        case 3:
            Tamanho = 3;
            break;
        case 4:
            Tamanho = 2;
            break;
        default:
            System.out.println("Tipo inválido.");
            return;
    }
    System.out.println("Informe a Linha [1-14] e a Coluna [A-N] para a posição do navio que você quer remover, seguido da rotação (0, 90, 180, 270): ");
    int LinhaR = Input.nextInt() - 1; // R = Remover
    String ColStrR = Input.next();
    int ColunaR = LetraParaNumero(ColStrR);
    int RotacaoR = Input.nextInt();

    System.out.println("Informe a Linha [1-14] e a Coluna [A-N] para a nova posição do navio, seguido da rotação (0, 90, 180, 270): ");
    int Linha = Input.nextInt() - 1;
    String ColStr = Input.next();
    //int Coluna = LetraParaNumero(ColStr);
    int Rotacao = Input.nextInt();

    RemoverNavio(Grade, Tamanho, LinhaR, ColunaR, RotacaoR);

    if (!Posicionar(Linha, ColStr, Rotacao, Tamanho, Grade)) {
        System.out.println("Não foi possível colocar o navio na nova posição. Tentando reposicionar o navio antigo.");
        ColocarNavios(Grade, Jogador, Tamanho, 1);
    }
}

private void RemoverNavio(int[][] Grade, int Tamanho, int Linha, int Coluna, int Rotacao) {
    if (Rotacao == 0) { // Horizontal para a direita
        for (int i = Coluna; i < Coluna + Tamanho; i++) {
            Grade[Linha][i] = 0;
        }
    } else if (Rotacao == 90) { // Vertical para baixo
        for (int i = Linha; i < Linha + Tamanho; i++) {
            Grade[i][Coluna] = 0;
        }
    } else if (Rotacao == 180) { // Horizontal para a esquerda
        for (int i = Coluna; i > Coluna - Tamanho; i--) {
            Grade[Linha][i] = 0;
        }
    } else if (Rotacao == 270) { // Vertical para cima
        for (int i = Linha; i > Linha - Tamanho; i--) {
            Grade[i][Coluna] = 0;
        }
    }
}

    public boolean Posicionar(int PosL, String PosC, int Rotacao, int Tamanho, int[][] Grade) {
        int IntC = LetraParaNumero(PosC);
        if (IntC == -1) {
            System.out.println("Letra inválida: " + PosL);
            return false;
        }

        if (!PosicaoValida(PosL, IntC, Tamanho, Rotacao, Grade)) {
            System.out.println("Não foi possível colocar o barco na posição: " + PosL + PosC + " com rotação " + Rotacao);
            return false;
        }

        if (Rotacao == 0) { // Horizontal para a direita
            for (int i = IntC; i < IntC + Tamanho; i++) {
                Grade[PosL][i] = 1;
            }
        } else if (Rotacao == 90) { // Vertical para baixo
            for (int i = PosL; i < PosL + Tamanho; i++) {
                Grade[i][IntC] = 1;
            }
        } else if (Rotacao == 180) { // Horizontal para a esquerda
            for (int i = IntC; i > IntC - Tamanho; i--) {
                Grade[PosL][i] = 1;
            }
        } else if (Rotacao == 270) { // Vertical para cima
            for (int i = PosL; i > PosL - Tamanho; i--) {
                Grade[i][IntC] = 1;
            }
        } else {
            System.out.println("Rotação inválida. Use 0, 90, 180 ou 270 graus.");
            return false;
        }

        System.out.println("Barco colocado na posição: " + PosL + (PosC + 1) + " com rotação " + Rotacao);
        return true;
    }

    public void BarcoAfundado(int[][] Grade, int Linha, int Coluna) {
        // Verificar na horizontal
        boolean afundadoHorizontal = true;
        int c = Coluna;
        while (c < TamanhoDaGrade && Grade[Linha][c] == 2) {
            c++;
        }
        if (c - Coluna >= 5) {
            for (int i = Coluna; i < c; i++) {
                if (Grade[Linha][i] != 2) {
                    afundadoHorizontal = false;
                    break;
                }
            }
        } else {
            afundadoHorizontal = false;
        }
    
        // Verificar na vertical
        boolean afundadoVertical = true;
        int l = Linha;
        while (l < TamanhoDaGrade && Grade[l][Coluna] == 2) {
            l++;
        }
        if (l - Linha >= 5) {
            for (int i = Linha; i < l; i++) {
                if (Grade[i][Coluna] != 2) {
                    afundadoVertical = false;
                    break;
                }
            }
        } else {
            afundadoVertical = false;
        }
    
        // Se afundadoHorizontal ou afundadoVertical for true, significa que o barco está completamente afundado
        if (afundadoHorizontal || afundadoVertical) {
            // Marcar as posições adjacentes como água ('X')
            for (int i = Linha - 1; i <= Linha + 1; i++) {
                for (int j = Coluna - 1; j <= Coluna + 1; j++) {
                    if (i >= 0 && i < TamanhoDaGrade && j >= 0 && j < TamanhoDaGrade) {
                        if (Grade[i][j] == 0) {
                            Grade[i][j] = 3;
                        }
                    }
                }
            }
        }
    }
    

}