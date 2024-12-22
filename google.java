import { NextAuthOptions } from "next-auth"
import GoogleProvider from "next-auth/providers/google"
import { PrismaAdapter } from "@auth/prisma-adapter"
import { prisma } from "@/lib/prisma"

const authOptions: NextAuthOptions = {
  adapter: PrismaAdapter(prisma),
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID as string,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET as string,
      authorization: {
        params: {
          scope: "openid email profile https://www.googleapis.com/auth/fitness.activity.read https://www.googleapis.com/auth/fitness.location.read",
          prompt: "consent",
          access_type: "offline",
          response_type: "code",
        },
      },
    }),
  ],
  callbacks: {
    async signIn({ account, profile }) {
      console.log("account", account, "profile", profile)
      return true
    },
    async jwt({ token, account, profile }) {
      if (account) {
        return { ...token, accessToken: account.access_token }
      }
      return token
    },
    async session({ session, token }) {
      console.log("session", session, "token", token)
      session.user.accessToken = token.accessToken as string
      return session
    },
  },
  pages: {
    signIn: "/login",
  },
  session: {
    strategy: "jwt",
    maxAge: 7 * 24 * 60 * 60, // 7 days
  },
}

export default authOptions
import { signIn } from "next-auth/react"
import { signOut } from "next-auth/react"

const signin = () => {
    return (
        <div className='flex items-center space-x-2'>
            <button
                className='border p-2 rounded-xl hover:bg-gray-200'
                onClick={() => signIn("google")}
            >
                使用 Google 登入
            </button>
            <button className='border p-2 rounded-xl hover:bg-gray-200' onClick={() => signOut()}>
                登出
            </button>
        </div>
    )
}

export default signin
const formatTime = (type: string, time: string) => {
    // TimeMillis -> Time
    // 1694534400000 -> xxxx-xx-xx xx:xx:xx zh-TW
    // in out
    if (type === "in") {
        // xxxx-xx-xx -> TimeMillis zh-TW
        const date = new Date(time)
        console.log("date", date.getTime() - 8 * 60 * 60 * 1000)
        return date.getTime() - 8 * 60 * 60 * 1000
    }
    if (type === "out") {
        const date = new Date(parseInt(time))
        return date.toLocaleString("zh-TW", { timeZone: "Asia/Taipei" })
    }
}

return (
    <div>
        <h1>Content</h1>
        <div className='space-x-5 p-4 text-center'>
            起始日期
            <input
                type='date'
                name=''
                id=''
                className='ml-2'
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
            />
            結束日期
            <input
                type='date'
                name=''
                id=''
                className='ml-2'
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
            />
            間隔時間
            <select name='' id='' onChange={(e) => setInterval(parseInt(e.target.value))}>
                <option value='1800000'>30分鐘</option>
                <option value='3600000'>1小時</option>
                <option value='7200000'>2小時</option>
                <option value='14400000'>4小時</option>
                <option value='28800000'>8小時</option>
                <option value='43200000'>12小時</option>
                <option value='86400000'>24小時</option>
            </select>
        </div>
    </div>
)
<div className=''>
  {startDate} {"~"} {endDate}
  {isLoading && <div>Loading...</div>}
  {isError && <div>{error?.message}</div>}
  {/* data?.bucket[0]?.dataset[0]?.point[0] && */}
  <div id='main' style={{ width: "100%", height: "600px" }}></div>
  {/* } */}
  {data?.bucket[0]?.dataset[0] &&
    data?.bucket.map((item) => {
      return (
        <div key={item.startTimeMillis}>
          <p>
            開始時間: {formatTime("out", item.startTimeMillis)}
          </p>
          <p>
            結束時間: {formatTime("out", item.endTimeMillis)}
          </p>
          {/* 三個 value 平均值: {" "} */}
          <p>
            平均心率: {item.dataset[0].point[0]?.value[0].fpVal.toFixed(0)}
          </p>
          <p>
            最高心率: {item.dataset[0].point[0]?.value[1].fpVal}
          </p>
          <p>
            最低心率: {item.dataset[0].point[0]?.value[2].fpVal}
          </p>
        </div>
      );
    })}
  <pre>資料{JSON.stringify(data, null, 1)}</pre>
</div>
export default Content
