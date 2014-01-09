#ifndef TCPLISTEN_H
#define TCPLISTEN_H

#include <glm/glm.hpp>
#include <QTcpSocket>
#include <QThread>
#include <QNetworkSession>
#include "Receiver.hpp"
#include <string>
#include <sstream>

class Receiver;

using namespace std;

class TcpListen : public QThread{
	Q_OBJECT

	public:
		TcpListen(Receiver *rcvr);
		void run();	

	private:
		QTcpSocket *tcpSocket;
		Receiver *rcvr;
		QNetworkSession *networkSession;

	private slots:
		void onConnected();
		void onDisconnected();
		void onReadyRead();
};

#endif