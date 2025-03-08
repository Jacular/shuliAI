import SwiftUI
import shared

struct ContentView: View {
	 @StateObject private var viewModel = ChatViewModel()

      var body: some View {
          ChatScreen(viewModel: viewModel)
              .onAppear {
                  // 初始化KMM
                  KoinApplication.start()
                  // 加载历史记录
                  viewModel.loadHistory(sessionId: "default_session")
              }
      }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}