Como buildar:
1) Vinculer o firestore no projeto
2)  interface ApiService {
    @POST("API_SENPULSE_AQUI")
    suspend fun postRequest(@Body request: PostRequest): Response<Unit>
}
Nessa linha substitua o API_SENPULSE_AQUI pela string enviada na atividade
3) Gere o APK
OBS: Ambos foram removidos por motivo de segurança, pois qualquer um que tenha essa URL consegue enviar um SMS para qualquer pessoa.

Como funciona:
1) Faça o login na aplicação (SMS)
2) Adicione o contato de alguém que já tenha feito o login na aplicação também no celular.
3) Envie a mensagem.

Irei enviar o APK na atividade também, com a api sms e firestore funcionando normalmente.

Alunos: Diogo
