package org.projeto.javafxmaven.Controles;

import org.projeto.javafxmaven.modelo.Juridico;
import org.projeto.javafxmaven.modelo.Trabalhador;
import org.projeto.javafxmaven.modelo.Usuario;

public class Sessao {
    public class SessaoUsuario {
        private static Usuario usuario;
        private static Juridico juridico;
        private static Trabalhador trabalhador;

        public static Trabalhador getTrabalhador() {
            return trabalhador;
        }

        public static void setTrabalhador(Trabalhador trabalhador) {
            SessaoUsuario.trabalhador = trabalhador;
        }

        public static void setUsuario(Usuario u) {
            usuario = u;
            juridico = null;
        }

        public static void setJuridico(Juridico j) {
            juridico = j;
            usuario = null;
        }

        public static boolean isJuridico() {
            return juridico != null;
        }

        public static Usuario getUsuario() {
            return usuario;
        }

        public static Juridico getJuridico() {
            return juridico;
        }

        public static void limpar() {
            usuario = null;
            juridico = null;
        }

    }
}
