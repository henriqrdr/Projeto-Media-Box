package playermp3thread;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.*;
/**
	 * Manipula threads para executar as músicas
	 */
	public class AdvancedPlayerThread extends Thread {
		
		private AdvancedPlayer player;
		//private AdvancedPlayer aPlayer;
		
		public AdvancedPlayerThread(AdvancedPlayer player) {
			this.player = player;
		}
		
		/**
		 * Cria uma thread para tocar uma música
		 */
		public void run() {
			try {
				player.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void pause() {
		}
		
		/**
		 * Encerra a thread que toca música
		 */
		public void end() {
			try {
				player.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}